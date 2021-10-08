package com.igenius.androidstories.app.stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.R

@Story
fun example1(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View {
    return inflater.inflate(R.layout.activity_main, container, false)
}

@Story
fun example2(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View {
    return inflater.inflate(R.layout.activity_main, container, false)
}