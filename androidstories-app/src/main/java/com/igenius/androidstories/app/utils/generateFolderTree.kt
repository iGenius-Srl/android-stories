package com.igenius.androidstories.app.utils

import com.igenius.androidstories.app.models.StoriesFolder
import com.igenius.androidstories.app.models.AndroidStory

fun generateFolderTree(stories: List<AndroidStory>) =
    StoriesFolder().also { root ->
        stories.forEach { generateCompletePath(root, it) }
    }

private fun generateCompletePath(root: StoriesFolder, story: AndroidStory) {
    var lastFolder = root
    story.path.forEach {
        lastFolder = lastFolder.getOrGenerateSubfolder(it)
    }
    lastFolder.updateOrAdd(story)
}


