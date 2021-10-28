package com.igenius.androidstories.app

import android.app.Application
import com.igenius.androidstories.app.data.StoriesProvider

abstract class StoriesApp: Application() {

    abstract val storiesProvider: StoriesProvider
}