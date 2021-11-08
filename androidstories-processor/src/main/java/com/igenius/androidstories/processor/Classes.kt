package com.igenius.androidstories.processor

import com.squareup.kotlinpoet.ClassName


private const val MAIN_PACKAGE = "com.igenius.androidstories.app"

val GENERIC_FRAGMENT = ClassName("androidx.fragment.app", "Fragment")

val STORY_FRAGMENT = ClassName(MAIN_PACKAGE, "StoryFragment")

val ASYNC_STORY_FRAGMENT = ClassName(MAIN_PACKAGE, "AsyncStoryFragment")

val STORY_LAYOUT = ClassName(MAIN_PACKAGE, "LayoutStory")

val ASYNC_STORY_LAYOUT = ClassName(MAIN_PACKAGE, "AsyncLayoutStory")

private const val MODELS_PACKAGE = "com.igenius.androidstories.app.models"

val STORIES_PROVIDER_INTERFACE = ClassName(MODELS_PACKAGE, "StoriesProvider")

val ANDROID_FRAGMENT_STORY_MODEL = ClassName(MODELS_PACKAGE, "AndroidFragmentStory")

val ANDROID_ASYNC_FRAGMENT_STORY_MODEL = ClassName(MODELS_PACKAGE, "AndroidAsyncFragmentStory")

// Async providers
const val ANNOTATIONS_PACKAGE = "com.igenius.androidstories.annotations"

val ASYNC_VARIANT_PROVIDER = ClassName(ANNOTATIONS_PACKAGE, "AsyncVariantProvider")

val ASYNC_CONTEXT_VARIANT_PROVIDER = ClassName(MODELS_PACKAGE, "AsyncContextVariantProvider")