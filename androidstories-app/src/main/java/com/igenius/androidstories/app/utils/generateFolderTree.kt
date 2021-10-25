package com.igenius.androidstories.app.utils

import com.igenius.androidstories.app.StoriesFolder
import com.igenius.androidstories.app.ViewStory

fun generateFolderTree(stories: List<ViewStory>) =
    StoriesFolder("root").also { root ->
        stories.forEach { generateCompletePath(root, it) }
    }

private fun generateCompletePath(root: StoriesFolder, story: ViewStory) {
    var lastFolder = root
    story.path.forEach {
        lastFolder = lastFolder.getOrGenerateSubfolder(it)
    }
    lastFolder.updateOrAdd(story)
}


