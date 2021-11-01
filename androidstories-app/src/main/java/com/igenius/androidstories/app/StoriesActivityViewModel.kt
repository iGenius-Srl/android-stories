package com.igenius.androidstories.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.igenius.androidstories.app.data.StoriesFolder
import com.igenius.androidstories.app.data.StoriesProvider
import com.igenius.androidstories.app.data.StoryNode
import com.igenius.androidstories.app.data.ViewStory
import com.igenius.androidstories.app.utils.generateFolderTree

class StoriesActivityViewModel: ViewModel() {

    private var root: StoriesFolder? = null
        set(value) {
            field = value
            openedFolders.clear()
            showingItems = value?.children?.toMutableList() ?: mutableListOf()
            updateList()
        }

    private val openedFolders: MutableList<StoriesFolder> = mutableListOf()
    private var showingItems: MutableList<StoryNode> = mutableListOf()

    private val _showingListLiveData = MutableLiveData<List<NodeItemModel>>()
    val showingListLiveData: LiveData<List<NodeItemModel>> = _showingListLiveData

    private val _selectedStoryLiveData = MutableLiveData<ViewStory?>(null)
    val selectedStoryLiveData: LiveData<ViewStory?> = _selectedStoryLiveData

    fun toggleStory(story: ViewStory?) {
        _selectedStoryLiveData.value = story.takeIf {
            it != _selectedStoryLiveData.value
        }
        updateList()
    }

    private val _fullViewLiveData = MutableLiveData(false)
    val fullViewLiveData: LiveData<Boolean> get() = _fullViewLiveData

    fun toggleFullView() {
        setFullView(!(_fullViewLiveData.value ?: true))
    }

    fun setFullView(value: Boolean) {
        _fullViewLiveData.value = value
    }

    fun fetchRootFolder(provider: StoriesProvider) {
        root?.let { return }
        val all = provider.stories.sortedBy { it.completePath }
        root = generateFolderTree(all)
    }

    fun toggleFolder(folder: StoriesFolder) {
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

    private fun updateList() = root?.let { root ->
        showingItems.map {
            NodeItemModel(
                node = it,
                rootDistance = root.distance(it),
                isOpen = openedFolders.contains(it),
                selected = it == selectedStoryLiveData.value
            )
        }.let { _showingListLiveData.value = it }
    }
}