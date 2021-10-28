package com.igenius.androidstories.app

import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.StoryNode
import com.igenius.androidstories.app.data.TestStory
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.utils.generateFolderTree
import org.junit.Test

import org.junit.Assert.*

class FolderTreeTests {

    @Test
    fun `without folders it returns just the list`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "Title 2"),
            TestStory(2, "Title 3"),
            TestStory(3, "Title 4"),
        )

        val result = generateFolderTree(stories)

        assertArrayEquals(stories.toTypedArray(), result.children.toTypedArray())
    }

    @Test
    fun `should create a folder`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "folder/Title 2")
        )

        val result = generateFolderTree(stories)

        result.assertSubNode(stories[0], "Title 1")
        result.assertSubNode(stories[1], "folder", "Title 2")
    }


    @Test
    fun `should catch repetitive dividers`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "/folder//Title 2")
        )

        val result = generateFolderTree(stories)

        result.assertSubNode(stories[0], "Title 1")
        result.assertSubNode(stories[1], "folder", "Title 2")
    }

    @Test
    fun `should use the same folder for different stories`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "folder/Title 2"),
            TestStory(2, "folder/Title 3")
        )

        val result = generateFolderTree(stories)

        result.assertSubNode(stories[0], "Title 1")
        result.assertSubNode(stories[1], "folder", "Title 2")
        result.assertSubNode(stories[2], "folder", "Title 3")
    }

    @Test
    fun `should generate the full path`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "folder/subfolder/Title 2"),
            TestStory(2, "folder/Title 3")
        )

        val result = generateFolderTree(stories)

        result.assertSubNode(stories[0], "Title 1")
        result.assertSubNode(stories[1], "folder", "subfolder", "Title 2")
        result.assertSubNode(stories[2], "folder", "Title 3")
    }

    @Test
    fun `should recreate the complete directories three`() {
        val stories = listOf(
            TestStory(0, "Title 1"),
            TestStory(1, "folder/subfolder/Title 2"),
            TestStory(2, "folder/Title 3"),
            TestStory(3, "folder2/Title 4"),
            TestStory(4, "folder/subfolder/Title 5"),
            TestStory(5, "folder/subfolder/subfolder2/Title 6"),
            TestStory(6, "folder2/subfolder/subfolder2/Title 7"),
        )

        val result = generateFolderTree(stories)

        result.assertSubNode(stories[0], "Title 1")
        result.assertSubNode(stories[1], "folder", "subfolder", "Title 2")
        result.assertSubNode(stories[2], "folder", "Title 3")
        result.assertSubNode(stories[3], "folder2", "Title 4")
        result.assertSubNode(stories[4], "folder", "subfolder", "Title 5")
        result.assertSubNode(stories[5],  "folder", "subfolder", "subfolder2", "Title 6")
        result.assertSubNode(stories[6],  "folder2", "subfolder", "subfolder2", "Title 7")
    }

    private fun StoryNode.assertSubNode(story: TestStory, vararg path: String) {
        var result: StoryNode = this
        path.forEach { result = result.subNode(it) }
        assertEquals(story.id, (result as TestStory).id)
    }

    private fun StoryNode.subNode(title: String): StoryNode {
        return (this as StoriesFolder).children.find { it.title == title }!!
    }
}