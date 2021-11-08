package com.igenius.androidstories.app.story

import android.app.Application
import androidx.lifecycle.*
import com.igenius.androidstories.annotations.AsyncVariantProvider
import com.igenius.androidstories.app.models.AsyncContextVariantProvider
import kotlinx.coroutines.*

class StoryDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedVariant = MutableLiveData<String?>(null)
    val selectedVariant: LiveData<String?> get() = _selectedVariant

    private val _fetchedVariantData = MutableLiveData<Pair<String, *>?>(null)
    val fetchedVariantData: LiveData<Pair<String, *>?> get() = _fetchedVariantData

    private val _fetchVariantLoading = MutableLiveData(false)
    val fetchVariantLoading: LiveData<Boolean> get() = _fetchVariantLoading

    private var providerJob: Job? = null

    var provider: AsyncVariantProvider<*>? = null
    set(value) {
        field = value
        providerJob?.cancel()
        _fetchVariantLoading.value = false
    }

    fun selectVariant(variant: String) {
        _selectedVariant.value = variant
        provider ?: return

        providerJob?.cancel()
        _fetchVariantLoading.value = true

        providerJob = viewModelScope.launch(Dispatchers.IO) {

            val data = fetchData(variant)

            (Dispatchers.Main) {
                _fetchedVariantData.value = variant to data
                _fetchVariantLoading.value = false
            }
        }
    }

    private suspend fun fetchData(variant: String) = when(provider) {
        is AsyncContextVariantProvider<*> ->
            (provider as AsyncContextVariantProvider<*>).fetchData(getApplication(), variant)
        else -> provider?.fetchData(variant)
    }

    override fun onCleared() {
        super.onCleared()
        providerJob?.cancel()
        provider
            ?.takeIf { it.cacheLifeTime == AsyncVariantProvider.CacheLifeTime.VIEW_MODEL }
            ?.clearCache()
    }
}