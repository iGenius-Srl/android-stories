package com.igenius.androidstories.app

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.igenius.androidstories.app.story.StoryDetailsFragment

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