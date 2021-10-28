package com.igenius.androidstories.app.data

import androidx.fragment.app.Fragment

interface StoriesProvider {
    val stories: List<FragmentStory>
}

interface ViewStory : StoryNode {
    val id: Int
    val description: String?
}

interface FragmentStory: ViewStory {
    fun generateFragment(): Fragment
}