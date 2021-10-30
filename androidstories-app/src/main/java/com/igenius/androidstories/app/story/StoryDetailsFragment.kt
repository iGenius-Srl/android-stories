package com.igenius.androidstories.app.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentStoryBinding
import com.igenius.androidstories.app.R
import com.igenius.androidstories.app.StoryFragment
import java.lang.IllegalStateException

class StoryDetailsFragment : Fragment() {
    private var binding : FragmentStoryBinding? = null
    private var storyId: Int = 0

    private val viewModel: StoryDetailsViewModel by viewModels()

    private var storyFragment: StoryFragment?
        get() = childFragmentManager.findFragmentByTag("story_fragment") as? StoryFragment
        set(value) {
            value?.let {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.story_placeholder, it, "story_fragment")
                    .commit()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storyId = arguments?.let { StoryDetailsFragmentArgs.fromBundle(it) }?.storyId
            ?: throw IllegalStateException("StoryDetailsFragment: No storyId provided")
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

        val story = (requireContext().applicationContext as StoriesApp).storiesProvider.stories[storyId]
        storyFragment = story.generateFragment()

        binding?.description?.text = story.description ?: "No description"

        binding?.buttonVariant?.apply {
            val popup = PopupMenu(context, this)
            popup.setOnMenuItemClickListener { menItem ->
                viewModel.selectVariant(menItem.title.toString())
                return@setOnMenuItemClickListener false
            }
            story.variants.forEach { variant -> popup.menu.add(variant) }
            setOnClickListener { popup.show() }
        }

        if(viewModel.selectedVariant.value.isNullOrEmpty())
            viewModel.selectVariant(story.variants[0])

        viewModel.selectedVariant.observe(viewLifecycleOwner) {
            it?.let { selectVariant(it) }
        }
    }

    private fun selectVariant(variant: String) {
        binding?.buttonVariant?.text = variant
        storyFragment?.selectVariant(variant)
    }
}