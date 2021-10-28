package com.igenius.androidstories.app.utils

import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.StoryNode
import com.igenius.androidstories.app.data.ViewStory

fun generateFolderTree(stories: List<ViewStory>) =
    StoriesFolder().also { root ->
        stories.forEach { generateCompletePath(root, it) }
    }

private fun generateCompletePath(root: StoriesFolder, story: ViewStory) {
    var lastFolder = root
    story.path.forEach {
        lastFolder = lastFolder.getOrGenerateSubfolder(it)
    }
    lastFolder.updateOrAdd(story)
}


