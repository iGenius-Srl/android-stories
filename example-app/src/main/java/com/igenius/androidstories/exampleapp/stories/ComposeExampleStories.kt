package com.igenius.androidstories.exampleapp.stories

import androidx.compose.material3.Text
import com.igenius.androidstories.annotations.AsyncVariant
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.story.AsyncComposeStory
import com.igenius.androidstories.app.story.ComposeStory

@Story(
    "Compose/Simple",
    variants = ["Variant 1", "Variant 2"]
)
val compose_story = ComposeStory {
    Text(it)
}


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