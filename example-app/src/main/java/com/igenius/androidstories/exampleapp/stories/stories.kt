package com.igenius.androidstories.exampleapp.stories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.material3.Text
import androidx.fragment.app.Fragment
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.story.LayoutStory
import com.igenius.androidstories.app.story.StoryFragment
import com.igenius.androidstories.app.story.ComposeStory
import com.igenius.androidstories.exampleapp.R
import com.igenius.androidstories.exampleapp.setBackgroundColorRes

/**
 * This is an example of the shortest story ([LayoutStory]):
 * a LayoutStory variable that defines the title by the variable name
 * and the content by a layout resource
 */
@Story val simple_story = LayoutStory(R.layout.simple_story)

/**
 * Complete usage example of an [LayoutStory]:
 * The story defines a title, a description and a list of variants (different version of the same story).
 */
@Story(
    title = "Button/Base Button",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"]
)
val button_story = LayoutStory(R.layout.button_story) { variant ->
    when (variant) {
        "Red" -> android.R.color.holo_red_light
        else -> android.R.color.holo_blue_bright
    }.let(findViewById<View>(R.id.button)::setBackgroundColorRes)
}

/**
 * A story can be even defined with a simple fragment class
 */
@Story(
    title = "Fragment/Native Fragment",
    description = "Example of a Fragment story, variants cannot be used"
)
class NativeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.simple_story, container, false)
}

/**
 * [StoryFragment] allows you to create custom fragments that are being updated
 * at every story variant change throughout [onVariantSelected].
 */
@Story(
    title = "Fragment/Story Fragment",
    description = "This is a fragment story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"]
)
class ExampleFragment: StoryFragment() {

    override fun getLayoutRes() = R.layout.button_story

    override fun onVariantSelected(variant: String) {
        view?.findViewById<Button>(R.id.button)?.setBackgroundColorRes(
            when (variant) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    }
}
/**
 * [ComposeStory] allows you to create a composable story which receives the current variant as parameter.
 */
@Story(
    "Compose/Simple",
    variants = ["Variant 1", "Variant 2"]
)
val compose_story = ComposeStory { variant ->
    Text(variant)
}