package com.igenius.androidstories.app.models

import android.content.Context
import com.igenius.androidstories.annotations.AsyncVariantProvider
import com.igenius.androidstories.app.AsyncStoryFragment
import com.igenius.androidstories.app.StoryFragment
import java.lang.IllegalStateException

interface StoriesProvider {
    val stories: List<AndroidFragmentStory>
}

interface AndroidStory : StoryNode {
    val id: Int
    val description: String?
    val variants: Array<String>
}

interface AndroidFragmentStory: AndroidStory {
    fun generateFragment(): StoryFragment
}

interface AndroidAsyncFragmentStory<T>: AndroidFragmentStory {
    override fun generateFragment(): AsyncStoryFragment<T>
    val dataProvider: AsyncVariantProvider<T>
}

abstract class AsyncContextVariantProvider<T>(
    cacheLifeTime: CacheLifeTime = CacheLifeTime.VIEW_MODEL
): AsyncVariantProvider<T>(cacheLifeTime) {

    suspend fun fetchData(context: Context, variant: String): T {
        cached(variant)?.let { return@fetchData it }

        return provide(context, variant).also { cache(variant, it) }
    }

    abstract suspend fun provide(context: Context, variant: String): T

    final override suspend fun fetchData(variant: String): T {
        throw IllegalStateException("please use the fetchData method with context")
    }

    final override suspend fun provide(variant: String): T {
        throw IllegalStateException("please use the provide method with context")
    }
}