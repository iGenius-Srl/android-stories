package com.igenius.androidstories.app.models

import android.content.Context
import androidx.fragment.app.Fragment
import com.igenius.androidstories.annotations.AsyncVariantProvider
import com.igenius.androidstories.app.story.AsyncStoryFragment

/**
 * Interface used by the processor to provide stories to the stories-app
 */
interface StoriesProvider {
    val stories: List<AndroidFragmentStory>
}

/**
 * Generic story type
 */
interface AndroidStory : StoryNode {

    // unique story identifier
    val id: Int

    // story description
    val description: String?

    /**
     * List of variants, used to create the variants meno of a story,
     * If the defined story is com.igenius.androidstories.app.StoryFragment,
     * when the user will select one of these, onVariantSelected will be invoked
     */
    val variants: Array<String>
}

/**
 * Defines a story of a Fragment
 */
interface AndroidFragmentStory : AndroidStory {
    fun generateFragment(): Fragment
}

/**
 * Defines a story of a AndroidFragmentStory,
 * @param <T> type of the async loaded data for a variant
 */
interface AndroidAsyncFragmentStory<T> : AndroidFragmentStory {

    // returns an instance of a AsyncStoryFragment<T> (implemented by the processor)
    override fun generateFragment(): AsyncStoryFragment<T>

    // instance of a variant provider
    val dataProvider: AsyncVariantProvider<T>
}

/**
 * Class that defines a provider of data that receives the current android.content.Context
 * @param <T> Type of the provided data
 * @param cacheLifeTime is the life time specification of fetched data
 * (see {@link com.igenius.androidstories.annotations.AsyncVariantProvider.CacheLifeTime} enum)
 */
abstract class AsyncContextVariantProvider<T>(
    cacheLifeTime: CacheLifeTime = CacheLifeTime.VIEW_MODEL
) : AsyncVariantProvider<T>(cacheLifeTime) {

    /**
     * Method to fetch data for a provided variant
     * @param context
     * @param variant to fetch
     * @returns fetched data
     */
    abstract suspend fun provide(context: Context, variant: String): T

    suspend fun fetchData(context: Context, variant: String): T {
        cached(variant)?.let { return@fetchData it }

        return provide(context, variant).also { cache(variant, it) }
    }

    final override suspend fun fetchData(variant: String): T {
        throw IllegalStateException("please use the fetchData method with context")
    }

    final override suspend fun provide(variant: String): T {
        throw IllegalStateException("please use the provide method with context")
    }
}