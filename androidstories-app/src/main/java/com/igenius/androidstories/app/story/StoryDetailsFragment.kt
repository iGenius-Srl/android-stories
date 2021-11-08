package com.igenius.androidstories.app.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.igenius.androidstories.app.databinding.FragmentStoryBinding
import java.lang.IllegalStateException
import com.igenius.androidstories.app.*
import com.igenius.androidstories.app.models.AndroidAsyncFragmentStory
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
        private set(value) {
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

    private var storyFragment: Fragment?
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

        viewModel.provider ?: run {
            viewModel.provider = (story as? AndroidAsyncFragmentStory<*>)?.dataProvider
        }

        storyFragment = story.generateFragment()

        binding?.toolbar?.run {
            title = story.title
            subtitle = story.description
            setNavigationOnClickListener {
                (activity as StoriesActivity).closeStory()
            }

            inflateMenu(R.menu.story_details_fragment_menu)
            menu.findItem(R.id.variants_item).subMenu.run {
                story.variants.forEach { variant ->
                    add(variant).isCheckable = true
                }
            }
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.fullView_item -> (activity as StoriesActivity).setFullView(true)
                    R.id.closeFullView_item -> (activity as StoriesActivity).setFullView(false)
                    else -> if(it.itemId != R.id.variants_item && story.variants.contains(it.title))
                        viewModel.selectVariant(it.title.toString())
                }
                return@setOnMenuItemClickListener false
            }
        }


        if (viewModel.selectedVariant.value.isNullOrEmpty())
            viewModel.selectVariant(story.variants[0])

        viewModel.selectedVariant.observe(viewLifecycleOwner) {
            it?.let { selectVariant(it) }
        }

        viewModel.fetchedVariantData.observe(viewLifecycleOwner) {
            it?.let {
                val data = it.second ?: return@let
                (storyFragment as? AsyncStoryFragment<*>)?.loadVariantData(it.first, data)
            }
        }

        viewModel.fetchVariantLoading.observe(viewLifecycleOwner) {
            loadingLayout = it
        }

        applyConfiguration(configuration)
    }

    private fun selectVariant(variant: String) {
        binding?.toolbar?.menu?.findItem(R.id.variants_item)?.run {
            title = variant
            subMenu.forEach {
                it.isChecked = it.title == variant
            }
        }
        (storyFragment as? StoryFragment)?.variant = variant
    }

    private fun applyConfiguration(config: StoryDetailsConfiguration) = binding?.toolbar?.apply {
        setNavigationIcon(
            if(config.isFullView) R.drawable.ic_baseline_arrow_back_24
            else R.drawable.ic_baseline_close_24
        )
        menu.findItem(R.id.fullView_item).isVisible = config.canChangeFullView && !config.isFullView
        menu.findItem(R.id.closeFullView_item).isVisible = config.canChangeFullView && config.isFullView
    }

    private val fadeIn by lazy { AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in) }
    private val fadeOut by lazy { AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out) }

    private var loadingLayout = false
        set(value) = binding?.run {
            field = value
            progressIndicator.isVisible = value
            storyContainer.isVisible = !value
            progressIndicator.startAnimation(if(value) fadeIn else fadeOut)
            storyContainer.startAnimation(if(!value) fadeIn else fadeOut)
        } ?: Unit

    companion object {
        private const val STORY_ID = "storyId"
        private const val CONFIGURATION = "configuration"
        fun getInstance(storyId: Int, configuration: StoryDetailsConfiguration) = StoryDetailsFragment().also {
            it.configuration = configuration
            it.storyId = storyId
        }
    }
}