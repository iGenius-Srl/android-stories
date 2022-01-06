package com.igenius.androidstories.app.favourites

import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach

class SelectedStoryManager(
    val application: Application
) {

    private val applicationPrefsKey = "${application.packageName}.prefs"
    private val selectedStoryPrefsKey = "prefs.selected"

    private val _selectedStoryFlow = MutableStateFlow(readSelectedId())
    val selectedStoryFLow: Flow<Int?> get() = _selectedStoryFlow.onEach(::saveSelectedId)

    fun toggle(id: Int?) {
        _selectedStoryFlow.value = id?.takeIf { _selectedStoryFlow.value != it }
    }

    fun cleanWithCurrentStories(stories: List<Int>) {
        _selectedStoryFlow.value = _selectedStoryFlow.value.takeIf { stories.contains(it) }
    }

    private fun saveSelectedId(id: Int?) = application
        .getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)
        .edit()
        .apply {
            id?.let { putInt(selectedStoryPrefsKey, it) }
                ?: remove(selectedStoryPrefsKey)
        }
        .apply()

    private fun readSelectedId(): Int? = application
        .getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)
        .takeIf { it.contains(selectedStoryPrefsKey) }
        ?.getInt(selectedStoryPrefsKey, 0)
}