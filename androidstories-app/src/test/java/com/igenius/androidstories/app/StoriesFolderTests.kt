package com.igenius.androidstories.app

import com.igenius.androidstories.app.models.StoriesFolder
import org.junit.Assert.assertEquals
import org.junit.Test

class StoriesFolderTests {

    @Test
    fun `should add a folder`() {
        val root = StoriesFolder()
        val folder = StoriesFolder("child")
        root.updateOrAdd(folder)

        assertEquals(folder, root.children[0])
    }

    @Test
    fun `should update the folder`() {
        val root = StoriesFolder()

        val folder1 = StoriesFolder("child")
        root.updateOrAdd(folder1)

        val folder2 = StoriesFolder("child")
        root.updateOrAdd(folder2)

        assertEquals(1, root.children.size)
        assertEquals(folder2, root.children[0])
    }

    @Test
    fun `should get an existing folder`() {
        val root = StoriesFolder()

        val folder = StoriesFolder("child")
        root.updateOrAdd(folder)

        val result = root.getOrGenerateSubfolder("child")

        assertEquals(1, root.children.size)
        assertEquals(folder, result)
    }

    @Test
    fun `should get a new folder`() {
        val root = StoriesFolder()

        val folder = StoriesFolder("child")
        root.updateOrAdd(folder)

        val result = root.getOrGenerateSubfolder("child2")

        assertEquals(2, root.children.size)
        assert(root.children.contains(result))
        assert(root.children.contains(folder))
    }

    @Test
    fun `should return true if parent`() {
        val root = StoriesFolder()
        val folder = root.getOrGenerateSubfolder("folder")
        val subfolder = folder.getOrGenerateSubfolder("subfolder")

        // root -> folder = true
        assert(root.isParent(folder))

        // root -> folder -> subfolder = true
        assert(root.isParent(subfolder))

        // folder -> subfolder = true
        assert(folder.isParent(subfolder))

        // invalid parent = false
        assert(!subfolder.isParent(folder))

        // same folder = false
        assert(!folder.isParent(folder))
    }

    @Test
    fun `should return the correct distance`() {
        val root = StoriesFolder()
        val folder = root.getOrGenerateSubfolder("folder")
        val subfolder = folder.getOrGenerateSubfolder("subfolder")

        // root -> folder = 1
        assertEquals(1, root.distance(folder))

        // root -> folder -> subfolder = 2
        assertEquals(2, root.distance(subfolder))

        // folder -> subfolder = 1
        assertEquals(1, folder.distance(subfolder))

        // invalid parent = -1
        assertEquals(-1, subfolder.distance(folder))

        // same folder = 0
        assertEquals(0, folder.distance(folder))
    }
}