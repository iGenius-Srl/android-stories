package com.igenius.androidstories.exampleapp.stories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.igenius.androidstories.annotations.AsyncVariant
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.story.AsyncLayoutStory
import com.igenius.androidstories.app.story.AsyncStoryFragment
import com.igenius.androidstories.app.story.LayoutStory
import com.igenius.androidstories.app.story.StoryFragment
import com.igenius.androidstories.app.models.AsyncContextVariantProvider
import com.igenius.androidstories.exampleapp.R
import com.igenius.androidstories.exampleapp.setBackgroundColorRes
import kotlinx.coroutines.delay

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

@Story(
    title = "Fragment/Async Fragment",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
class AsyncExampleFragment: AsyncStoryFragment<Test>() {

    override fun getLayoutRes() = R.layout.button_story

    override fun onVariantLoaded(variant: String, data: Test) {
        view?.findViewById<Button>(R.id.button)?.setBackgroundColorRes(
            when (data.foo) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    }
}

@Story(
    title = "AsyncLayout/Simple story",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_layout_story = AsyncLayoutStory<Test>(R.layout.button_story) { _, data ->
    findViewById<Button>(R.id.button)?.setBackgroundColorRes(
        when (data.foo) {
            "Red" -> android.R.color.holo_red_light
            else -> android.R.color.holo_blue_bright
        }
    )
}

@Story(
    title = "AsyncLayout/Simple story without loader",
    description = "This is a story without a loader",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_layout_story_without_loader = AsyncLayoutStory<Test>(
    R.layout.button_story,
    preventUiLoader = true
) { _, data ->
    findViewById<Button>(R.id.button)?.setBackgroundColorRes(
        when (data.foo) {
            "Red" -> android.R.color.holo_red_light
            else -> android.R.color.holo_blue_bright
        }
    )
}

class AsyncExampleFragmentProvider: AsyncContextVariantProvider<Test>() {
    override suspend fun provide(context: Context, variant: String): Test {
        delay(3000)
        return Test(variant)
    }
}

data class Test (val foo: String)