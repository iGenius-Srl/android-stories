package com.igenius.androidstories.exampleapp

import com.igenius.androidstories.app.StoriesApp
import com.igenius.androidstories.app.StoriesProvider
import com.igenius.androidstories.exampleapp.stories.AppStoriesProvider

class ExampleStoriesApp: StoriesApp() {
    override val storiesProvider: StoriesProvider
        get() = AppStoriesProvider()
}