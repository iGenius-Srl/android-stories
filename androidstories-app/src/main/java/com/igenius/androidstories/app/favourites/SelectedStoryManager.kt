package com.igenius.androidstories.app.favourites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach

class SelectedStoryManager(
    private val preferencesManager: IPreferencesManager
) {

    private val selectedStoryPrefsKey = "prefs.selected"

    private val _selectedStoryFlow = MutableStateFlow(readSelectedId())
    val selectedStoryFLow: Flow<Int?> get() = _selectedStoryFlow.onEach(::saveSelectedId)

    fun toggle(id: Int?) {
        _selectedStoryFlow.value = id?.takeIf { _selectedStoryFlow.value != it }
    }

    fun cleanWithCurrentStories(stories: List<Int>) {
        _selectedStoryFlow.value = _selectedStoryFlow.value.takeIf { stories.contains(it) }
    }

    private fun saveSelectedId(id: Int?) = preferencesManager
        .preferences
        .edit()
        .apply {
            id?.let { putInt(selectedStoryPrefsKey, it) }
                ?: remove(selectedStoryPrefsKey)
        }
        .apply()

    private fun readSelectedId(): Int? = preferencesManager
        .preferences
        .takeIf { it.contains(selectedStoryPrefsKey) }
        ?.getInt(selectedStoryPrefsKey, 0)
}