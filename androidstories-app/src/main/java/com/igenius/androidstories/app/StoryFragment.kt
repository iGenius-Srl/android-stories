package com.igenius.androidstories.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

open class StoryFragment: Fragment() {

    private val bundleArgs: Bundle get() {
        return arguments ?: Bundle().also { arguments = it }
    }

    private var currentVariant: String?
        get() = bundleArgs.getString("variant")
        set(value) { bundleArgs.putString("variant", value) }

    fun selectVariant(newVariant: String) {
        currentVariant = newVariant
        view?.let { onVariantSelected(newVariant) }
    }

    open fun getLayoutRes(): Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(getLayoutRes(), container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentVariant?.let { onVariantSelected(it) }
    }

    open fun onVariantSelected (variant: String) {}
}