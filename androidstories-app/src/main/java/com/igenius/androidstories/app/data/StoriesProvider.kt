package com.igenius.androidstories.app.data

import com.igenius.androidstories.app.StoryFragment

interface StoriesProvider {
    val stories: List<FragmentStory>
}

interface ViewStory : StoryNode {
    val id: Int
    val description: String?
    val variants: Array<String>
}

interface FragmentStory: ViewStory {
    fun generateFragment(): StoryFragment
}