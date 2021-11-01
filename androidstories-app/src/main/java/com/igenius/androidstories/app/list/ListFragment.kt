package com.igenius.androidstories.app.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.igenius.androidstories.app.R
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.StoryFragment
import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.databinding.FragmentListBinding
import com.igenius.androidstories.app.story.StoryDetailsFragment
import com.igenius.androidstories.app.story.StoryDetailsViewModel
import com.igenius.androidstories.app.utils.generateFolderTree

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListFragmentViewModel by viewModels()

    private val isListDetails get() = binding.storyDetailsPlaceholder?.let { true } ?: false

    private var detailsFragment: StoryDetailsFragment?
        get() = childFragmentManager.findFragmentByTag("details_fragment") as? StoryDetailsFragment
        set(value) {
            if(!isListDetails) return
            value?.let {
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.story_details_placeholder, it, "details_fragment")
                    .commit()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchRootFolder((requireContext().applicationContext as StoriesApp).storiesProvider)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)

        val adapter = StoriesAdapter {
            when(it) {
                is ViewStory -> onStorySelect(it)
                is StoriesFolder -> viewModel.toggleFolder(it)
            }
        }
        binding.recycler.adapter = adapter

        viewModel.showingListLiveData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        return binding.root
    }

    private fun onStorySelect(story: ViewStory) {
        val action = ListFragmentDirections.actionListToStory(story.id, story.title)
        if(isListDetails) {
            detailsFragment = StoryDetailsFragment().also { it.arguments = action.arguments }
        } else {
            findNavController().navigate(action)
        }
    }

}