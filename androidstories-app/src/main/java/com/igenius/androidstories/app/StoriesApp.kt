package com.igenius.androidstories.app

import android.app.Application

abstract class StoriesApp: Application() {

    abstract val storiesProvider: StoriesProvider
}