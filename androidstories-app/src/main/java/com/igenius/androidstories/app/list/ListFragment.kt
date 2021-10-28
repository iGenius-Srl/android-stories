package com.igenius.androidstories.app.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentListBinding
import com.igenius.androidstories.app.utils.generateFolderTree

class ListFragment : Fragment() {

    private val stories by lazy {
        val all = (requireContext().applicationContext as StoriesApp).storiesProvider.stories.sortedBy { it.completePath }
        return@lazy generateFolderTree(all)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListBinding.inflate(inflater, container, false)

        binding.recycler.adapter = FolderManager(stories) { story ->
            val action = ListFragmentDirections.actionListToStory(story.id, story.title)
            findNavController().navigate(action)
        }.adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )

        return binding.root
    }

}