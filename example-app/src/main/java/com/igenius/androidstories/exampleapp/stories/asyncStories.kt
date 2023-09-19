package com.igenius.androidstories.exampleapp.stories

import android.content.Context
import android.widget.Button
import androidx.compose.material3.Text
import com.igenius.androidstories.annotations.AsyncVariant
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.models.AsyncContextVariantProvider
import com.igenius.androidstories.app.story.AsyncComposeStory
import com.igenius.androidstories.app.story.AsyncLayoutStory
import com.igenius.androidstories.app.story.AsyncStoryFragment
import com.igenius.androidstories.exampleapp.R
import com.igenius.androidstories.exampleapp.setBackgroundColorRes
import kotlinx.coroutines.delay

/**
 * This is an example of a simple story with an asynchronous data retrieve.
 * an AsyncStoryFragment class which defines the button style by the variant name and the content by a layout resource.
 * Is important to declare an [AsyncVariant] annotation to specify the provider that will resolve the variant string to a domain model
 * (in this example we are using [AsyncExampleFragmentProvider]).
 */
@Story(
    title = "Fragment/Async Fragment",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
class AsyncExampleFragment: AsyncStoryFragment<Test>() {

    override fun getLayoutRes() = R.layout.button_story

    override fun onVariantLoaded(variant: String, data: Test?) {
        view?.findViewById<Button>(R.id.button)?.setBackgroundColorRes(
            when (data?.foo) {
                "Red" -> android.R.color.holo_red_light
                else -> android.R.color.holo_blue_bright
            }
        )
    }
}

/**
 * This is an example of a simple story with an asynchronous data retrieve
 * an AsyncLayoutStory object that defines the button style by the variable name
 * and the content by a layout resource.
 * Is important to declare an [AsyncVariant] annotation to specify the provider that will resolve the variant to a domain model
 * (in this example is [AsyncExampleFragmentProvider]).
 */
@Story(
    title = "AsyncLayout/Simple story",
    description = "This is a story with different variants, press on the right to select ones",
    variants = ["Red", "Blue"],
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_layout_story = AsyncLayoutStory<Test>(R.layout.button_story) { _, data ->
    findViewById<Button>(R.id.button)?.setBackgroundColorRes(
        when (data?.foo) {
            "Red" -> android.R.color.holo_red_light
            else -> android.R.color.holo_blue_bright
        }
    )
}

/**
 * This is an example of a simple story with an asynchronous data retrieve
 * an AsyncLayoutStory object that defines the button style by the variable name
 * and the content by a layout resource.
 * In this example there is [AsyncLayoutStory.preventUiLoader] as true, this allows to see the story content when the provided data is still loading (data is null).
 * Is important to declare an [AsyncVariant] annotation to specify the provider that will resolve the variant to a domain model
 * (in this example is [AsyncExampleFragmentProvider]).
 */
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
    findViewById<Button>(R.id.button)?.run {
        setBackgroundColorRes(
            when (data?.foo) {
                "Red" -> android.R.color.holo_red_light
                "Blue" -> android.R.color.holo_blue_bright
                else -> android.R.color.darker_gray
            }
        )
        isEnabled = data != null
    }
}
/**
 * This is an example of a simple story with an asynchronous data retrieve in a compose view
 * an AsyncLayoutStory object that defines the button style by the variable name
 * and the content by a layout resource.
 * In this example there is [AsyncComposeStory.preventUiLoader] as true, this allows to see the story content when the provided data is still loading (data is null).
 * Is important to declare an [AsyncVariant] annotation to specify the provider that will resolve the variant to a domain model
 * (in this example is [AsyncExampleFragmentProvider]).
 */
@Story(
    "Compose/Async",
    variants = ["Variant 1", "Variant 2"]
)
@AsyncVariant(AsyncExampleFragmentProvider::class)
val async_compose_story = AsyncComposeStory<Test>(preventUiLoader = true) {
    it?.let {
        Text(it.foo)
    } ?: Text("Loading")
}

class AsyncExampleFragmentProvider: AsyncContextVariantProvider<Test>() {
    override suspend fun provide(context: Context, variant: String): Test {
        delay(3000)
        return Test(variant)
    }
}

data class Test (val foo: String)