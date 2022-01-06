package com.igenius.androidstories.app.utils

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.igenius.androidstories.app.R

fun View.animate(
    @AnimRes animRes: Int,
    fillAfter: Boolean = false,
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {},
    onRepeat: () -> Unit = {}
): Animation = AnimationUtils.loadAnimation(context, animRes).also {
    it.fillAfter = fillAfter
    it.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) = onStart()
        override fun onAnimationEnd(p0: Animation?) = onEnd()
        override fun onAnimationRepeat(p0: Animation?) = onRepeat()
    })
    startAnimation(it)
}

fun <T: Fragment> FragmentManager.commitFragment(@IdRes viewId: Int, tag: String, fragment: T?) {
    fragment?.let {
        beginTransaction()
            .replace(viewId, it, tag)
            .commit()
    } ?: run {
        retrieveFragment<T>(tag)?.let {
            beginTransaction()
                .remove(it)
                .commit()
        }
    }
}

fun <T: Fragment> FragmentManager.retrieveFragment(tag: String): T?
    = findFragmentByTag(tag) as? T

data class ThemeColors(
    val colorPrimary: Int,
    val colorSecondary: Int,
    val textColorPrimary: Int,
)

val Context.themeColors: ThemeColors
get() {
    val typedValue = TypedValue()
    val attrs = intArrayOf(
        R.attr.colorPrimary,
        R.attr.colorSecondary,
        android.R.attr.textColorPrimary,
    )
    return theme.obtainStyledAttributes(typedValue.data, attrs).let {
        val themeColors = ThemeColors(
            it.getColor(attrs.indexOf(R.attr.colorPrimary), 0),
            it.getColor(attrs.indexOf(R.attr.colorSecondary), 0),
            it.getColor(attrs.indexOf(android.R.attr.textColorPrimary), 0),
        )
        it.recycle()
        themeColors
    }
}