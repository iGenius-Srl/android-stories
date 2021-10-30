package com.igenius.androidstories.app.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StoryDetailsViewModel : ViewModel() {

    private val _selectedVariant = MutableLiveData<String?>(null)
    val selectedVariant: LiveData<String?> get() = _selectedVariant

    fun selectVariant(variant: String) {
        _selectedVariant.value = variant
    }
}