package com.igenius.androidstories.app.story

import android.view.View

/**
 * A data class to define a story based on a layout file.
 */
data class LayoutStory(
    /**
     * Layout id to inflate
     */
    val layoutId: Int,

    /**
     * Function executed when the view is initially inflated (variant will be the default value)
     *  and when the user will select a different variant. See [StoryFragment.onVariantSelected]
     *  The this will be the inflated view.
     *  @param this The inflated view
     *  @param variant the selected variant
     */
    val onVariantSelected: View.(variant: String) -> Unit = {}
)

/**
 * A data class to define a story based on a layout file with an async operation.
 * Any story defined with this class needs to be annotated with [AsyncVariant]
 */
data class AsyncLayoutStory<T>(

    /**
     * Layout id to inflate
     */
    val layoutId: Int,

    /**
     * True to prevent a default loader that hides the story during the fetching of data for a selected variant.
     * see [AsyncStoryFragment.preventUiLoader]
     */
    val preventUiLoader: Boolean = false,

    /**
     *  Executed when the provider resolve a data for a given variant
     *  see [AsyncStoryFragment.onVariantLoaded]
     *  @param this The inflated view
     *  @param variant The selected variant
     *  @param data The data fetched from the provider, null during the loading state
     */
    val onVariantLoaded: View.(variant: String, data: T?) -> Unit = { _, _ -> }
)