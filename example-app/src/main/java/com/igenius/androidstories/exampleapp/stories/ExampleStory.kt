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
import com.igenius.androidstories.app.AsyncLayoutStory
import com.igenius.androidstories.app.AsyncStoryFragment
import com.igenius.androidstories.app.LayoutStory
import com.igenius.androidstories.app.StoryFragment
import com.igenius.androidstories.app.models.AsyncContextVariantProvider
import com.igenius.androidstories.exampleapp.R
import kotlinx.coroutines.delay

@Story val layout_story_example = LayoutStory(R.layout.button_story)

@Story(
    title = "Button red blu LayoutStory",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"]
)
val story2 = LayoutStory(R.layout.button_story) { variant ->
    findViewById<Button>(R.id.button)?.setBackgroundColor(
        ContextCompat.getColor(
            context,
            when (variant) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    )
}

@Story
class NativeFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.button_story, container, false)
}

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

@Story(
    title = "Async/button red blu",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
class AsyncExampleFragment: AsyncStoryFragment<Test>() {

    override fun getLayoutRes() = R.layout.button_story

    override fun onVariantLoaded(variant: String, data: Test) {
        view?.findViewById<Button>(R.id.button)?.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                when (data.foo) {
                    "Red" -> android.R.color.holo_red_light
                    else -> android.R.color.holo_blue_bright
                }
            )
        )
    }
}

@Story(
    title = "Async/layout story",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_layout_story = AsyncLayoutStory<Test>(R.layout.button_story) { _, data ->
    findViewById<Button>(R.id.button)?.setBackgroundColor(
        ContextCompat.getColor(
            context,
            when (data.foo) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    )
}

@Story(
    title = "Async/layout story without loader",
    description = "This is a story without a loader",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_layout_story_without_loader = AsyncLayoutStory<Test>(
    R.layout.button_story,
    preventUiLoader = true
) { _, data ->
    findViewById<Button>(R.id.button)?.setBackgroundColor(
        ContextCompat.getColor(
            context,
            when (data.foo) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    )
}

class AsyncExampleFragmentProvider: AsyncContextVariantProvider<Test>() {
    override suspend fun provide(context: Context, variant: String): Test {
        delay(3000)
        return Test(variant)
    }
}

data class Test (val foo: String)