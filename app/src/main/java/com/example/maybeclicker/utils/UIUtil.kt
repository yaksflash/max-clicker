package com.example.maybeclicker.utils

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.maybeclicker.R
import android.view.View

// Функция для Fragment — можно вызывать просто updateUI()
fun Fragment.updateUI() {
    if (!isAdded) return
    updateUI(requireActivity())
}

// Основная функция — обновляет UI
fun updateUI(activity: Activity) {
    // Получаем SharedPreferences из текущей Activity
    val sharedPrefs = activity.getSharedPreferences("General", Context.MODE_PRIVATE)
    val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
    val sharedPref = activity.getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

    val maxGold = sharedPref.getInt("MAX_GOLD", 0)
    val coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
    val userLevel = sharedPref.getInt("USER_LEVEL", 1)

    // Находим TextView на Activity
    val topLevel = activity.findViewById<TextView>(R.id.textViewOfLevel)
    val topGold = activity.findViewById<TextView>(R.id.textViewOfMaxGold)
    val topCoins = activity.findViewById<TextView>(R.id.textViewOfCoins)

    // Обновляем их
    topLevel?.text = userLevel.toString()
    topGold?.text = maxGold.toString()
    topCoins?.text = formatCoinsExtendedLong(coinCount.toDouble()).toString()
}

// Новая функция — обновление UI из диалогового окна
fun updateUIFromDialog(dialogView: View) {
    val activity = dialogView.context as? Activity ?: return

    val sharedPrefs = activity.getSharedPreferences("General", Context.MODE_PRIVATE)
    val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
    val sharedPref = activity.getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

    val maxGold = sharedPref.getInt("MAX_GOLD", 0)
    val coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
    val userLevel = sharedPref.getInt("USER_LEVEL", 1)

    // Находим TextView уже на Activity, а не в диалоге
    val topLevel = activity.findViewById<TextView>(R.id.textViewOfLevel)
    val topGold = activity.findViewById<TextView>(R.id.textViewOfMaxGold)
    val topCoins = activity.findViewById<TextView>(R.id.textViewOfCoins)

    topLevel?.text = userLevel.toString()
    topGold?.text = maxGold.toString()
    topCoins?.text = formatCoinsExtendedLong(coinCount.toDouble()).toString()
}