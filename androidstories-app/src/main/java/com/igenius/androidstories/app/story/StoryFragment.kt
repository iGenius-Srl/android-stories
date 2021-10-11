package com.igenius.androidstories.app.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentStoryBinding
import com.igenius.androidstories.app.R


const val STORY_ID = "story_id"

/**
 * A simple [Fragment] subclass.
 * Use the [StoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StoryFragment : Fragment() {
    private var binding : FragmentStoryBinding? = null
    private var storyId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            storyId = it.getInt(STORY_ID)
        }
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

        childFragmentManager
            .beginTransaction()
            .replace(R.id.story_placeholder, story.generateFragment())
            .commit()
    }
}