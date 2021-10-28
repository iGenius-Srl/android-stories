package com.igenius.androidstories.annotations

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Story(
    val title: String = "",
    val description: String = "",
    val variants: Array<String> = ["Default"]
)