package com.igenius.androidstories.exampleapp

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun View.setBackgroundColorRes(@ColorRes colorRes: Int): Unit =
    setBackgroundColor(ContextCompat.getColor(context, colorRes))