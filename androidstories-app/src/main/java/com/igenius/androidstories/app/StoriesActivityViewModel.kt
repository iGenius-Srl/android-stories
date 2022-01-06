package com.igenius.androidstories.app

import android.app.Application
import androidx.lifecycle.*
import com.igenius.androidstories.app.favourites.FavouritesManager
import com.igenius.androidstories.app.models.StoriesFolder
import com.igenius.androidstories.app.models.StoriesProvider
import com.igenius.androidstories.app.models.StoryNode
import com.igenius.androidstories.app.models.AndroidStory
import com.igenius.androidstories.app.utils.generateFolderTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class StoriesActivityViewModel(application: Application) : AndroidViewModel(application) {

    private var root: StoriesFolder? = null
        set(value) {
            field = value
            openedFolders.clear()
            _showingListFlow.value = value?.children ?: emptyList()
        }

    private val favouritesManager = FavouritesManager(getApplication())
    private var allStories: List<AndroidStory> = emptyList()
        set(value) {
            field = value
            root = generateFolderTree(value)
            favouritesManager.cleanWithCurrentStories(value.map { it.id })
        }

    private val openedFolders: MutableList<StoriesFolder> = mutableListOf()

    private val selectedStoryIdFlow = MutableStateFlow<Int?>(null)
    val selectedStoryLiveData = selectedStoryIdFlow.asLiveData(viewModelScope.coroutineContext)

    private val _showingListFlow = MutableStateFlow<List<StoryNode>>(emptyList())
    val showingListLiveData: LiveData<List<NodeItemModel>> = _showingListFlow
        .map { nodes ->
            nodes.map {
                NodeItemModel.node(
                    node = it,
                    rootDistance = root?.distance(it) ?: 0,
                    isOpen = openedFolders.contains(it)
                )
            }
        }
        .combine(favouritesManager.favouritesFlow) { items, favouritesIds ->
            favouritesIds
                .mapNotNull { id -> allStories.find { it.id == id } }
                .map(NodeItemModel::favourite)
                .toMutableList()
                .also { it.addAll(items) }
        }
        .combine(selectedStoryIdFlow) { items, selectedId ->
            items.map { item ->
                val selected = selectedId?.let {
                    (item.node as? AndroidStory)?.id == selectedId
                } ?: false
                item.copy(selected = selected)
            }
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun toggleStory(story: AndroidStory?) {
        selectedStoryIdFlow.value = story?.id?.takeIf {
            it != selectedStoryIdFlow.value
        }
    }

    private val _fullViewLiveData = MutableLiveData(false)
    val fullViewLiveData: LiveData<Boolean> get() = _fullViewLiveData

    fun setFullView(value: Boolean) {
        _fullViewLiveData.value = value
    }

    fun fetchRootFolder(provider: StoriesProvider) {
        root?.let { return }
        allStories = provider.stories.sortedBy { it.completePath }
    }

    fun toggleFolder(folder: StoriesFolder) = _showingListFlow.updateValue { showingItems ->
        val currentIndex = showingItems.indexOf(folder)
        if(currentIndex == -1) return

        if(!openedFolders.contains(folder)) {
            openedFolders.add(folder)
            showingItems.addAll(currentIndex + 1, folder.children)
        } else {
            openedFolders.removeAll { folder == it || folder.isParent(it) }
            showingItems.removeAll { folder.isParent(it) }
        }
    }

    fun toggleFavourite(story: AndroidStory) = favouritesManager.toggle(story.id)
}

inline fun <T> MutableStateFlow<List<T>>.updateValue(action: (current: MutableList<T>) -> Unit) {
    value = value.toMutableList().also(action).toList()
}