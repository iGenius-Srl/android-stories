package com.igenius.androidstories.annotations

import kotlin.reflect.KClass

/**
 * Annotation to define a story, @Story can be assigned to:
 * Classes that extends
 *  - {@link androidx.fragment.app.Fragment}
 *  - {@link com.igenius.androidstories.app.story.StoryFragment}
 *  - {@link com.igenius.androidstories.app.story.AsyncStoryFragment}
 *
 *  Instances of
 *  - {@link com.igenius.androidstories.app.story.LayoutStory}
 *  - {@link com.igenius.androidstories.app.story.AsyncLayoutStory}
 */
@Retention(AnnotationRetention.SOURCE)
annotation class Story(

    /**
     * Title of the story, the default value is the annotated class name or annotated variable name
     * with "_" replaced by " ".
     */
    val title: String = "",

    /**
     * Description that will be shown as subtitle of the story's toolbar
     */
    val description: String = "",

    /**
     * Array of variants for the given story,
     * One of these values can be selected in the option menu of the story's toolbar,
     * The default selected variant will be the first element.
     * see {@link com.igenius.androidstories.app.story.StoryFragment#onVariantSelected(variant: String)}
     */
    val variants: Array<String> = ["Default"]
)

/**
 * Annotation to specify an AsyncDataProvider for the annotated story {@link AsyncVariantProvider}
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AsyncVariant constructor(
    /**
     * data provider ::class
     */
    val dataProvider: KClass<out AsyncVariantProvider<*>>
)

/**
 * Provider used to parse a given variant to a model object in a suspend function.
 * @param T Type of a data instance
 * @param cacheLifeTime Lifetime of a provided data to prevent heavy operations every variant user selection
 */
abstract class AsyncVariantProvider <T> (
    val cacheLifeTime: CacheLifeTime = CacheLifeTime.VIEW_MODEL
) {

    /**
     * Enum to define a lifetime of a provided data T
     */
    enum class CacheLifeTime {
        // Data will be generated every time (no cache)
        NONE,

        // Data will be generated every time the story details fragment is created
        VIEW_MODEL,

        // Data will be generated only the first time
        APPLICATION
    }

    /**
     * Function used to provide a specific data T based on a selected variant
     * @param variant Selected variant
     * @return An instance of T
     */
    protected abstract suspend fun provide(variant: String): T

    private val cachedData = if(cacheLifeTime != CacheLifeTime.NONE)
        mutableMapOf<String, T>() else null

    fun clearCache() { cachedData?.clear() }

    protected fun cache(variant: String, data: T) {
        cachedData?.put(variant, data)
    }

    protected fun cached(variant: String): T? = cachedData?.get(variant)

    open suspend fun fetchData(variant: String): T {
        cached(variant)?.let { return@fetchData it }

        return provide(variant).also { cache(variant, it) }
    }
}