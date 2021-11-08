package com.igenius.androidstories.annotations

import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Story(
    val title: String = "",
    val description: String = "",
    val variants: Array<String> = ["Default"]
)

@Target(AnnotationTarget.FIELD, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class AsyncVariant constructor(
    val dataProvider: KClass<out AsyncVariantProvider<*>>
)

abstract class AsyncVariantProvider <T> (
    val cacheLifeTime: CacheLifeTime = CacheLifeTime.VIEW_MODEL
) {
    enum class CacheLifeTime {
        NONE, VIEW_MODEL, APPLICATION
    }

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

    protected abstract suspend fun provide(variant: String): T
}