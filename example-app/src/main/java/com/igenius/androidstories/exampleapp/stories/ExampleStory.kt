package com.igenius.androidstories.exampleapp.stories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.exampleapp.R

@Story(
    title = "Button"
)
fun example1(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_story, container, false)

@Story(
    title = "Button secondary",
    description = "Example 2 description"
)
fun example2(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_secondary_story, container, false)

@Story(
    title = "Example a total fragment"
)
class ExampleFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.button_story, container, false)
}