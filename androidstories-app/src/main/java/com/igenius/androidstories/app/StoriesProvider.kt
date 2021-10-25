package com.igenius.androidstories.app

import androidx.fragment.app.Fragment

interface StoriesProvider {
    val stories: List<FragmentStory>
}

interface StoryNode {
    val completePath: String

    private val splitPath get() = completePath.split(DIVIDER).filterNot { it.isBlank() }

    val title: String
        get() = splitPath.last()

    val path: List<String>
        get() = splitPath.toMutableList().also { it.removeLast() }

    companion object {
        const val DIVIDER = "/"
    }
}

data class StoriesFolder(
    override val completePath: String,
) : StoryNode {

    private val _children: MutableList<StoryNode> = mutableListOf()

    val children get() = _children.toList().sortedBy { it.completePath }

    fun updateOrAdd(node: StoryNode): Boolean {
        val toUpdate = _children.find { it.completePath == node.completePath }
        return toUpdate?.let {
            val index = _children.indexOf(it)
            _children.removeAt(index)
            _children.add(index, node)
            true
        } ?: run {
            _children.add(node)
            false
        }
    }

    fun getOrGenerateSubfolder(name: String): StoriesFolder =
        (_children.find { it.completePath == name } as? StoriesFolder)
            ?: StoriesFolder(name).also {
                _children.add(it)
            }
}

interface ViewStory : StoryNode {
    val id: Int
    val description: String?
}

interface FragmentStory: ViewStory {
    fun generateFragment(): Fragment
}