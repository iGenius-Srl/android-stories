package com.igenius.androidstories.app.story

import androidx.compose.runtime.Composable

class ComposeStory(
    val content: @Composable (variant: String) -> Unit,
)

class AsyncComposeStory<T>(
    val preventUiLoader: Boolean = false,
    val content: @Composable (variant: T?) -> Unit,
)