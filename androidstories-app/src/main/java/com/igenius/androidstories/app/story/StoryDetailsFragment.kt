package com.igenius.androidstories.app.story

import android.graphics.PorterDuff
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.internal.view.SupportMenuItem.SHOW_AS_ACTION_ALWAYS
import androidx.core.view.get
import androidx.fragment.app.viewModels
import com.igenius.androidstories.app.databinding.FragmentStoryBinding
import java.lang.IllegalStateException

import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.text.style.ForegroundColorSpan

import android.text.SpannableString
import com.igenius.androidstories.app.*
import com.igenius.androidstories.app.utils.commitFragment
import com.igenius.androidstories.app.utils.retrieveFragment
import java.io.Serializable

data class StoryDetailsConfiguration(
    val isFullView: Boolean,
    val canChangeFullView: Boolean
): Serializable

private const val STORY_FRAGMENT_TAG = "story_fragment"

class StoryDetailsFragment : Fragment() {
    private var binding: FragmentStoryBinding? = null

    var storyId: Int
        get() = arguments?.getInt(STORY_ID)
            ?: throw IllegalStateException("StoryDetailsFragment: No storyId provided")
        set(value) {
            arguments = (arguments ?: Bundle()).apply { putInt(STORY_ID, value) }
        }

    var configuration: StoryDetailsConfiguration
        get() = (arguments?.getSerializable(CONFIGURATION) as? StoryDetailsConfiguration)
            ?: throw IllegalStateException("StoryDetailsFragment: configuration not provided")
        set(value) {
            arguments = (arguments ?: Bundle()).apply { putSerializable(CONFIGURATION, value) }
            binding?.let { applyConfiguration(value) }
        }

    fun updateConfiguration (
        isFullView: Boolean? = null,
        canChangeFullView: Boolean? = null
    ) {
        val prevConfig = configuration
        configuration = StoryDetailsConfiguration(
            isFullView = isFullView ?: prevConfig.isFullView,
            canChangeFullView = canChangeFullView ?: prevConfig.canChangeFullView,
        )
    }

    private val viewModel: StoryDetailsViewModel by viewModels()

    private var storyFragment: StoryFragment?
        get() = childFragmentManager.retrieveFragment(STORY_FRAGMENT_TAG)
        set(value) {
            childFragmentManager.commitFragment(R.id.story_placeholder, STORY_FRAGMENT_TAG, value)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoryBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val story =
            (requireContext().applicationContext as StoriesApp).storiesProvider.stories[storyId]
        storyFragment = story.generateFragment()

        binding?.toolbar?.run {
            title = story.title
            subtitle = story.description
            setNavigationOnClickListener {
                (activity as StoriesActivity).closeStory()
            }
            val variantSubmenu = menu.addSubMenu(menuOptionText(story.variants[0]))
            variantSubmenu.item.setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
            story.variants.forEach { variant -> variantSubmenu?.add(variant) }
            setOnMenuItemClickListener {
                if(story.variants.contains(it.title))
                    viewModel.selectVariant(it.title.toString())
                return@setOnMenuItemClickListener false
            }

            menu.add("Fullscreen toggle").let {
                setIcon(it, R.drawable.ic_baseline_open_in_full_16)
                it.setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                it.setOnMenuItemClickListener {
                    (activity as StoriesActivity).toggleFullView()
                    return@setOnMenuItemClickListener false
                }
            }
        }


        if (viewModel.selectedVariant.value.isNullOrEmpty())
            viewModel.selectVariant(story.variants[0])

        viewModel.selectedVariant.observe(viewLifecycleOwner) {
            it?.let { selectVariant(it) }
        }

        applyConfiguration(configuration)
    }

    private fun selectVariant(variant: String) {
        binding?.toolbar?.menu?.getItem(0)?.title = menuOptionText(variant)
        storyFragment?.selectVariant(variant)
    }

    private fun applyConfiguration(configuration: StoryDetailsConfiguration) = binding?.toolbar?.apply {
        setNavigationIcon(
            if(configuration.isFullView) R.drawable.ic_baseline_arrow_back_24
            else R.drawable.ic_baseline_close_24
        )
        menu?.get(1)?.let {
            setIcon(
                it,
                if(configuration.isFullView) R.drawable.ic_baseline_close_fullscreen_16
                else R.drawable.ic_baseline_open_in_full_16
            )
            it.isVisible = configuration.canChangeFullView
        }
    }

    private val colorOnPrimary by lazy {
        TypedValue().also {
            requireContext().theme.resolveAttribute(R.attr.colorOnPrimary, it, true)
        }.data
    }

    private fun setIcon(menuItem: MenuItem, @DrawableRes icon: Int) {
        val drawable = ContextCompat.getDrawable(requireContext(), icon) ?: return
        menuItem.icon = DrawableCompat.wrap(drawable).apply {
            setTint(colorOnPrimary)
            setTintMode(PorterDuff.Mode.SRC_IN)
        }
    }

    private fun menuOptionText(text: String) = SpannableString(text).also {
        it.setSpan(ForegroundColorSpan(colorOnPrimary), 0, it.length, 0)
    }

    companion object {
        private const val STORY_ID = "storyId"
        private const val CONFIGURATION = "configuration"
        fun getInstance(storyId: Int, configuration: StoryDetailsConfiguration) = StoryDetailsFragment().also {
            it.configuration = configuration
            it.storyId = storyId
        }
    }
}