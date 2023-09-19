package com.igenius.androidstories.exampleapp.stories

import androidx.compose.material3.Text
import com.igenius.androidstories.annotations.Story
import com.igenius.androidstories.app.story.ComposeStory

@Story(
    variants = ["Variant 1", "Variant 2"]
)
val compose_story = ComposeStory {
    Text(it)
}