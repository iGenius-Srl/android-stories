package com.igenius.androidstories.app.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.igenius.androidstories.annotations.AsyncVariant

/**
 * A fragment class that allows the usage of variants.
 */
open class StoryFragment : Fragment() {

    private val bundleArgs: Bundle
        get() = arguments ?: Bundle().also { arguments = it }

    // Current selected variant
    var variant: String?
        get() = bundleArgs.getString(VARIANT_KEY)
        set(value) {
            bundleArgs.putString(VARIANT_KEY, value)
            view?.let { value?.let { onVariantSelected(it) } }
        }

    /**
     * Override this method to easily inflate the fragment view
     * @return Layout id to inflate
     */
    @LayoutRes
    open fun getLayoutRes(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = getLayoutRes()
        .takeIf { it != 0 }
        ?.let { inflater.inflate(it, container, false) }
        ?: super.onCreateView(inflater, container, savedInstanceState)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        variant?.let { onVariantSelected(it) }
    }

    /**
     * Method called when the user selects a new variant
     * and when the story is initially loaded with the default variant
     * @param variant Selected value
     */
    open fun onVariantSelected(variant: String) {}

    companion object {
        private const val VARIANT_KEY = "variant"
    }
}

/**
 * A fragment class that allows the usage of variants and async data loading for each variant.
 * Any story defined with this class needs to be annotated with [AsyncVariant]
 * in order to specify a provider class
 */
abstract class AsyncStoryFragment<T> : StoryFragment() {

    /**
     * True to prevent a default loader that hides the story during the fetching of data for a selected variant.
     */
    open val preventUiLoader = false

    /**
     * Executed when the provider resolve a data for a given variant
     * @param variant The selected variant
     * @param data The data fetched from the provider
     */
    abstract fun onVariantLoaded(variant: String, data: T)

    fun loadVariantData(variant: String, data: Any) {
        (data as? T)?.let { onVariantLoaded(variant, it) }
    }
}
