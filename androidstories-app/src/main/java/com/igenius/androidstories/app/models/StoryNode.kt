package com.igenius.androidstories.app.models

/**
 * Interface that defines a node (folder o story)
 */
interface StoryNode {

    // Node complete path
    val completePath: String

    // Path divided by / that includes title
    val splitPath get() = completePath.split(DIVIDER).filterNot { it.isBlank() }

    // Node's title
    val title: String
        get() = splitPath.last()

    // Path divided by / that doesn't include title
    val path: List<String>
        get() = splitPath.toMutableList().also { it.removeLast() }

    // splitPath without blank spaces
    val cleanPath get() = splitPath.joinToString(DIVIDER)

    companion object {
        const val DIVIDER = "/"
    }
}