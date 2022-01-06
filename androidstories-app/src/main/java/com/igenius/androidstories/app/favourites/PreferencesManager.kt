package com.igenius.androidstories.app.favourites

import android.content.Context
import android.content.SharedPreferences

interface IPreferencesManager {
    val preferences: SharedPreferences
}

class PreferencesManager(val ctxProvider: () -> Context): IPreferencesManager {

    private val applicationPrefsKey = "${ctxProvider().packageName}.prefs"

    override val preferences: SharedPreferences
        get() = ctxProvider().getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)
}