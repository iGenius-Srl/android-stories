package com.igenius.androidstories.exampleapp.stories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.StoryFragment
import com.igenius.androidstories.exampleapp.R

@Story(
    title = "Button/Primary"
)
fun example1(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_story, container, false)

@Story(
    title = "Button/Secondary",
    description = "Example 2 description",
)
fun example2(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_secondary_story, container, false)

@Story(
    title = "Button/Inner/Secondary",
    description = "Example 4 description"
)
fun example4(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_secondary_story, container, false)

@Story(
    title = "Button/ZetaInner/Secondary",
    description = "Example 5 description"
)
fun example5(
    inflater: LayoutInflater,
    container: ViewGroup?,
): View = inflater.inflate(R.layout.button_secondary_story, container, false)

@Story(
    title = "Button red blu",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"]
)
class ExampleFragment: StoryFragment() {

    override fun getLayoutRes() = R.layout.button_story

    override fun onVariantSelected(variant: String) {
        view?.findViewById<Button>(R.id.button)?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                when (variant) {
                    "Red" -> android.R.color.holo_red_light
                    else -> android.R.color.holo_blue_bright
                }
            )
        )
    }
}