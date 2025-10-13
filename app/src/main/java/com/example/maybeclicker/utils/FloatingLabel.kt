package com.example.maybeclicker.utils

import android.view.View
import android.widget.*

fun showFloatingText(textView: TextView, text: String) {
    textView.text = text
    textView.visibility = View.VISIBLE
    textView.alpha = 1f
    textView.translationY = 0f

    textView.animate()
        .translationYBy(-150f)
        .alpha(0f)
        .setDuration(700)
        .withEndAction { textView.visibility = View.GONE }
        .start()
}