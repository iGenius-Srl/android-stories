package com.igenius.androidstories.app

import androidx.fragment.app.Fragment

interface StoriesProvider {
    val stories: List<FragmentStory>
}

interface ViewStory {
    val id: Int
    val title: String
    val description: String?
}

interface FragmentStory: ViewStory {
    fun generateFragment(): Fragment
}