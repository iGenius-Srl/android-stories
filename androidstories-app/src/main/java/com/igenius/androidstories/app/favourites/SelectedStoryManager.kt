package com.igenius.androidstories.app.favourites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach

interface ISelectedStoryManager {
    val selectedStoryFLow: Flow<Int?>
    fun toggle(id: Int?)
    fun cleanWithCurrentStories(stories: List<Int>)
}

class SelectedStoryManager(
    private val preferencesManager: IPreferencesManager
): ISelectedStoryManager {

    private val selectedStoryPrefsKey = "prefs.selected"

    private val _selectedStoryFlow = MutableStateFlow(readSelectedId())
    override val selectedStoryFLow: Flow<Int?> get() = _selectedStoryFlow.onEach(::saveSelectedId)

    override fun toggle(id: Int?) {
        _selectedStoryFlow.value = id?.takeIf { _selectedStoryFlow.value != it }
    }

    override fun cleanWithCurrentStories(stories: List<Int>) {
        _selectedStoryFlow.value = _selectedStoryFlow.value.takeIf { stories.contains(it) }
    }

    private fun saveSelectedId(id: Int?) =
        preferencesManager.setInt(selectedStoryPrefsKey, id)

    private fun readSelectedId(): Int? =
        preferencesManager.getInt(selectedStoryPrefsKey)
}