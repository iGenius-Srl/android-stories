package com.igenius.androidstories.exampleapp.stories.innerstory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.exampleapp.R

@Story
fun example3(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View {
    return inflater.inflate(R.layout.activity_main, container, false)
}