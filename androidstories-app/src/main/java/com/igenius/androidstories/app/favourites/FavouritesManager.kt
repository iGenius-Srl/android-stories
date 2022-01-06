package com.igenius.androidstories.app.favourites

import android.app.Application
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import java.lang.Integer.parseInt

class FavouritesManager(
    val application: Application
) {

    private val applicationPrefsKey = "${application.packageName}.prefs"
    private val favouritesPrefsKey = "prefs.favourites"

    private val _favouritesFlow = MutableStateFlow(readIds())
    val favouritesFlow: Flow<List<Int>> get() = _favouritesFlow.onEach(::saveIds)

    fun toggle(id: Int) = updateIds {
        if(!it.remove(id)) it.add(id)
        it
    }

    fun cleanWithCurrentStories(stories: List<Int>) = updateIds {
        it.filter { id -> stories.contains(id) }
    }

    private fun updateIds(action: (current: MutableList<Int>) -> List<Int>) {
        _favouritesFlow.value = action(_favouritesFlow.value.toMutableList())
    }

    private fun saveIds(ids: List<Int>) = application
        .getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)
        .edit()
        .putString(favouritesPrefsKey, ids.joinToString(","))
        .apply()

    private fun readIds(): List<Int> = application
            .getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)
            .getString(favouritesPrefsKey, null)
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.map(::parseInt)
            ?: emptyList()
}