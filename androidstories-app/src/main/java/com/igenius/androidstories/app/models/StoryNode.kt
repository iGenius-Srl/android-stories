package com.igenius.androidstories.app.models

sealed interface StoryNode {
    val completePath: String

    val splitPath get() = completePath.split(DIVIDER).filterNot { it.isBlank() }

    val title: String
        get() = splitPath.last()

    val path: List<String>
        get() = splitPath.toMutableList().also { it.removeLast() }

    val cleanPath get() = splitPath.joinToString(DIVIDER)

    companion object {
        const val DIVIDER = "/"
    }
}