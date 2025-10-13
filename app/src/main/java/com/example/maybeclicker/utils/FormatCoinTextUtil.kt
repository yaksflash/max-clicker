package com.example.maybeclicker.utils

import java.text.DecimalFormat

fun formatCoinsExtended(value: Double): String {
    if (value < 1000) return value.toInt().toString()

    val suffixes = arrayOf(
        "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "Nn", "Dc"
    )

    var tempValue = value
    var suffixIndex = -1

    while (tempValue >= 1000 && suffixIndex < suffixes.size - 1) {
        tempValue /= 1000
        suffixIndex++
    }

    val integerPart = tempValue.toLong()
    val decimalPart = ((tempValue - integerPart) * 10).toInt() // только 1 знак после точки

    val formatted = if (decimalPart == 0) {
        integerPart.toString()
    } else {
        "$integerPart.$decimalPart"
    }

    return "$formatted${suffixes[suffixIndex]}"
}



fun formatCoinsExtendedLong(value: Double): String {
    if (value < 1000000) return value.toInt().toString()

    val suffixes = arrayOf(
        "K", "M", "B", "T", "Qa", "Qi", "Sx", "Sp", "Oc", "Nn", "Dc"
    )

    var tempValue = value
    var suffixIndex = -1

    while (tempValue >= 1000 && suffixIndex < suffixes.size - 1) {
        tempValue /= 1000
        suffixIndex++
    }

    val formatted = if (tempValue % 1.0 == 0.0) {
        tempValue.toLong().toString()
    } else {
        // обрезаем до 3 знаков после запятой без округления
        val str = tempValue.toString()
        val dotIndex = str.indexOf('.')
        if (dotIndex == -1) str
        else {
            val end = minOf(dotIndex + 4, str.length) // dot + 3 цифры
            str.substring(0, end)
        }
    }

    return "$formatted${suffixes[suffixIndex]}"
}