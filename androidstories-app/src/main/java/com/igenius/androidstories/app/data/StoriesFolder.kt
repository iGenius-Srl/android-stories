package com.igenius.androidstories.app.data

import com.igenius.androidstories.app.data.StoryNode.Companion.DIVIDER

class StoriesFolder(
    override val completePath: String = ROOT_FOLDER
) : StoryNode {

    private val _children: MutableList<StoryNode> = mutableListOf()

    val children get() = _children.toList().sortedWith(CompareChildren())

    override val cleanPath: String
        get() = if (isRoot) ROOT_FOLDER
        else super.cleanPath

    private val isRoot get() = completePath == ROOT_FOLDER

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
        (if (!isRoot) "$completePath$DIVIDER$name" else name)
            .let { completePath ->
                (_children.find { it.completePath == completePath } as? StoriesFolder)
                    ?: StoriesFolder(completePath).also {
                        _children.add(it)
                    }
            }

    fun distance(child: StoryNode) =
        if (child == this) 0
        else if (!isParent(child)) -1
        else child.cleanPath.removePrefix("$cleanPath$DIVIDER").split(DIVIDER).size

    fun isParent(child: StoryNode) = if (isRoot) true
    else child.cleanPath.startsWith("$cleanPath${DIVIDER}")

    private companion object {
        const val ROOT_FOLDER = ""
    }
}

private class CompareChildren : Comparator<StoryNode> {
    override fun compare(a: StoryNode, b: StoryNode): Int {
        val byType = compareType(a, b)
        return if(byType == 0) compareName(a, b) else byType
    }

    private fun compareType(a: StoryNode, b: StoryNode): Int = when {
        a::class == b::class -> 0
        a is StoriesFolder -> -1
        else -> 1
    }

    private fun compareName(a: StoryNode, b: StoryNode): Int =
        a.completePath.compareTo(b.completePath)
}