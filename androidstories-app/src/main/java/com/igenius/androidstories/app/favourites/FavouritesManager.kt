package com.igenius.androidstories.app.favourites

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach

interface IFavouritesManager {
    val favouritesFlow: Flow<List<Int>>
    fun toggle(id: Int)
    fun cleanWithCurrentStories(stories: List<Int>)
}

class FavouritesManager(
    private val preferencesManager: IPreferencesManager
): IFavouritesManager {

    private val favouritesPrefsKey = "prefs.favourites"

    private val _favouritesFlow = MutableStateFlow(readIds())
    override val favouritesFlow: Flow<List<Int>> get() = _favouritesFlow.onEach(::saveIds)

    override fun toggle(id: Int) = updateIds {
        if(!it.remove(id)) it.add(id)
        it
    }

    override fun cleanWithCurrentStories(stories: List<Int>) = updateIds {
        it.filter { id -> stories.contains(id) }
    }

    private fun updateIds(action: (current: MutableList<Int>) -> List<Int>) {
        _favouritesFlow.value = action(_favouritesFlow.value.toMutableList())
    }

    private fun saveIds(ids: List<Int>) = preferencesManager
        .setString(favouritesPrefsKey, ids.joinToString(","))

    private fun readIds(): List<Int> = preferencesManager
        .getString(favouritesPrefsKey)
        ?.split(",")
        ?.mapNotNull { it.toIntOrNull() }
        ?: emptyList()
}