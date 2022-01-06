package com.igenius.androidstories.app.favourites

import android.content.Context
import android.content.SharedPreferences

interface IPreferencesManager {
    fun getInt(key: String): Int?
    fun setInt(key: String, value: Int?)
    fun getString(key: String): String?
    fun setString(key: String, value: String?)
}

class PreferencesManager(val ctxProvider: () -> Context): IPreferencesManager {

    private val applicationPrefsKey = "${ctxProvider().packageName}.prefs"

    private val preferences: SharedPreferences
        get() = ctxProvider().getSharedPreferences(applicationPrefsKey, Context.MODE_PRIVATE)

    override fun getInt(key: String) = preferences
        .takeIf { it.contains(key) }
        ?.getInt(key, 0)

    override fun setInt(key: String, value: Int?) = preferences
        .edit()
        .apply {
            value?.let { putInt(key, it) }
                ?: remove(key)
        }
        .apply()

    override fun getString(key: String) = preferences
        .getString(key, null)

    override fun setString(key: String, value: String?) = preferences
        .edit()
        .putString(key, value)
        .apply()
}