package com.igenius.androidstories.app.story

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.databinding.FragmentStoryBinding
import com.igenius.androidstories.app.R
import java.lang.IllegalStateException

class StoryDetailsFragment : Fragment() {
    private var binding : FragmentStoryBinding? = null
    private var storyId: Int = 0

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
        val fragment = story.generateFragment()
        fragment.selectVariant(story.variants[0])

        binding?.description?.text = story.description ?: "No description"
        binding?.variantLabel?.text = story.variants[0]

        binding?.variantView?.let { variantView ->
            val menu = PopupMenu(context, variantView).apply {
                setOnMenuItemClickListener { menItem ->
                    binding?.variantLabel?.text = menItem.title.toString()
                    fragment.selectVariant(menItem.title.toString())
                    return@setOnMenuItemClickListener false
                }
                story.variants.forEach { variant -> menu.add(variant) }
            }

            variantView.setOnClickListener {
                menu.show()
            }
        }

        childFragmentManager
            .beginTransaction()
            .replace(R.id.story_placeholder, fragment)
            .commit()
    }
}