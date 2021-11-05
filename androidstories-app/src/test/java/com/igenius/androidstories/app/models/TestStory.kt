package com.igenius.androidstories.app.models

data class TestStory(
    override val id: Int,
    override val completePath: String,
    override val description: String = "",
) : AndroidStory