package com.igenius.androidstories.app

import android.app.Application
import com.igenius.androidstories.app.models.StoriesProvider

abstract class StoriesApp: Application() {

    abstract val storiesProvider: StoriesProvider

    val appName: String get() =
        applicationInfo.labelRes
            .takeIf { it != 0 }
            ?.let { getString(it) }
            ?: applicationInfo.nonLocalizedLabel.toString()
}