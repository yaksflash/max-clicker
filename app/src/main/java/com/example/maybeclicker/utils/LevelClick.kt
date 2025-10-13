package com.example.maybeclicker.utils

import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.content.edit
import com.example.maybeclicker.R
import com.example.maybeclicker.utils.updateUI

fun Fragment.setupLevelClick(
    levelLayout: LinearLayout?
) {
    levelLayout?.setOnClickListener {
        val dialogView = layoutInflater.inflate(R.layout.level_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val buttonBuy = dialogView.findViewById<Button>(R.id.buttonBuy)
        val buttonClose = dialogView.findViewById<Button>(R.id.buttonClose)

        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

        // Инициализация цены при открытии диалога
        val userLevel = sharedPref.getInt("USER_LEVEL", 1)
        val price = if (userLevel != 1) {
            50000.0 + 75000 * userLevel
        } else {
            50000.0
        }

        buttonBuy.text = formatCoinsExtended(price)

        buttonBuy.setOnClickListener {
            val currentBalance = sharedPref.getFloat("COIN_COUNT", 0f)
            val currentLevel = sharedPref.getInt("USER_LEVEL", 1)
            val maxGold = sharedPref.getInt("MAX_GOLD", 0)

            val currentPrice = if (currentLevel != 1) {
                50000.0 + 75000  * currentLevel
            } else {
                50000.0
            }

            val goldBonus = 5 + currentLevel * 5

            if (currentBalance >= currentPrice) {
                sharedPref.edit {
                    putInt("MAX_GOLD", maxGold + goldBonus)
                    putFloat("COIN_COUNT", 0f)
                    putFloat("COIN_MULTIPLIER", (1 + (currentLevel + 1) / 10).toFloat())
                    putInt("CLICK_POWER", 1)
                    putInt("USER_LEVEL", currentLevel + 1)
                    putInt("OFFLINE_REWARD", 0)
                }
                requireActivity().deleteSharedPreferences("${account}upgrades")

                val clickerText = requireActivity().findViewById<TextView>(R.id.countOfClicks)
                clickerText.text = 0.toString()

                Toast.makeText(context, "Вы успешно повысили свой уровень!", Toast.LENGTH_SHORT).show()
                updateUI()
                dialog.dismiss()

            } else {
                Toast.makeText(context, "Недостаточно средств для повышения уровня!", Toast.LENGTH_SHORT).show()
            }
        }

        buttonClose.setOnClickListener {
            Toast.makeText(context, "Бро, надо тапать больше!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}
