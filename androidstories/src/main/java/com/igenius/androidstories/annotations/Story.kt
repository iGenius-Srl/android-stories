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

interface AsyncVariantProvider <T> {
    suspend fun provide(variant: String): T
}