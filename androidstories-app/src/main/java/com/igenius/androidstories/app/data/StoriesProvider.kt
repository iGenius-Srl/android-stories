package com.igenius.androidstories.app.data

import android.content.Context
import com.igenius.androidstories.annotations.AsyncVariantProvider
import com.igenius.androidstories.app.AsyncStoryFragment
import com.igenius.androidstories.app.StoryFragment
import java.lang.IllegalStateException

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

interface AsyncFragmentStory<T>: FragmentStory {
    override fun generateFragment(): AsyncStoryFragment<T>
    val dataProvider: AsyncVariantProvider<T>
}

abstract class AsyncContextVariantProvider<T>: AsyncVariantProvider<T> {
    final override suspend fun provide(variant: String): T {
        throw IllegalStateException("please use the provide method with context")
    }
    abstract suspend fun provide(context: Context, variant: String): T
}