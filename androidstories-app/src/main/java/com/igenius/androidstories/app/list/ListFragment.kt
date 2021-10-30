package com.igenius.androidstories.app.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.databinding.FragmentListBinding
import com.igenius.androidstories.app.story.StoryDetailsViewModel
import com.igenius.androidstories.app.utils.generateFolderTree

class ListFragment : Fragment() {


    private val viewModel: ListFragmentViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchRootFolder((requireContext().applicationContext as StoriesApp).storiesProvider)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater, container, false)

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
        findNavController().navigate(action)
    }

}