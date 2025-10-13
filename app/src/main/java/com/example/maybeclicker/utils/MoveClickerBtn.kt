package com.example.maybeclicker.utils

import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ImageButton

fun moveButtonWithinRangeWithMargin(
    button: ImageButton,
    parentLayout: FrameLayout,
    marginDp: Float = 20f
) {
    parentLayout.post {
        // Перевод dp в пиксели
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            marginDp,
            button.resources.displayMetrics
        )

        // Ограничения по координатам с учётом отступа
        val maxX = (parentLayout.width - button.width - marginPx).coerceAtLeast(marginPx)
        val maxY = (parentLayout.height - button.height - marginPx).coerceAtLeast(marginPx)

        // Случайная позиция в диапазоне отступ — максимум (Float)
        val randomX = (marginPx + Math.random() * (maxX - marginPx)).toFloat()
        val randomY = (marginPx + Math.random() * (maxY - marginPx)).toFloat()

        button.x = randomX
        button.y = randomY

        // Повтор через 1.7 секунды
        button.postDelayed({ moveButtonWithinRangeWithMargin(button, parentLayout, marginDp) }, 1700)
    }
}
