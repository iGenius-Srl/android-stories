package com.igenius.androidstories.app.list

import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.StoryNode
import com.igenius.androidstories.app.data.ViewStory

class FolderManager(
    private val root: StoriesFolder,
    private val onSelect: (story: ViewStory) -> Unit
) {

    private val openedFolders: MutableList<StoriesFolder> = mutableListOf()
    private val showingItems: MutableList<StoryNode> = root.children.toMutableList()

    val adapter = StoriesAdapter {
        when(it) {
            is ViewStory -> onSelect(it)
            is StoriesFolder -> toggleFolder(it)
        }
    }

    init {
        updateList()
    }

    private fun toggleFolder(folder: StoriesFolder) {
        val currentIndex = showingItems.indexOf(folder)
        if(currentIndex == -1) return

        if(!openedFolders.contains(folder)) {
            openedFolders.add(folder)
            showingItems.addAll(currentIndex + 1, folder.children)
        } else {
            openedFolders.removeAll { folder == it || folder.isParent(it) }
            showingItems.removeAll { folder.isParent(it) }
        }
        updateList()
    }

    private fun updateList() = showingItems.map {
        NodeItemModel(
            node = it,
            rootDistance = root.distance(it),
            isOpen = openedFolders.contains(it)
        )
    }.let { adapter.submitList(it) }
}
