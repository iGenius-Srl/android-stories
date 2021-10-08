package com.igenius.androidstories.app

import androidx.fragment.app.Fragment

interface StoriesProvider {
    val stories: List<FragmentStory>
}

interface FragmentStory {
    val title: String
    val description: String?
    fun generateFragment(): Fragment
}