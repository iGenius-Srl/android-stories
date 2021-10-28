package com.igenius.androidstories.app.data

data class TestStory(
    override val id: Int,
    override val completePath: String,
    override val description: String = "",
) : ViewStory