package com.igenius.androidstories.exampleapp

import android.app.Application
import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.models.StoriesProvider
import com.igenius.androidstories.exampleapp.stories.AppStoriesProvider

class ExampleStoriesApp: Application(), StoriesApp {
    override val storiesProvider: StoriesProvider
        get() = AppStoriesProvider()
}