package com.igenius.androidstories.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class StoryFragment : Fragment() {

    private val bundleArgs: Bundle
        get() {
            return arguments ?: Bundle().also { arguments = it }
        }

    var variant: String?
        get() = bundleArgs.getString("variant")
        set(value) {
            bundleArgs.putString("variant", value)
            view?.let { value?.let { onVariantSelected(it) } }
        }

    open fun getLayoutRes(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutRes(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        variant?.let { onVariantSelected(it) }
    }

    open fun onVariantSelected(variant: String) {}
}

abstract class AsyncStoryFragment<T> : StoryFragment() {

    final override fun onVariantSelected(variant: String) {}

    fun loadVariantData(variant: String, data: Any) {
        (data as? T)?.let { onVariantLoaded(variant, it) }
    }

    abstract fun onVariantLoaded(variant: String, data: T)
}

data class LayoutStory(
    val layoutId: Int,
    val onVariantSelected: View.(variant: String) -> Unit = {}
)

data class AsyncLayoutStory<T>(
    val layoutId: Int,
    val onVariantLoaded: View.(variant: String, data: T) -> Unit = { _, _ -> }
)