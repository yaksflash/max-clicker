package com.example.maybeclicker.utils

import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import com.example.maybeclicker.R

fun Fragment.setupSettingsClick(
    settingsLayout: LinearLayout?) {
    settingsLayout?.setOnClickListener {
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

        val dialogView = layoutInflater.inflate(R.layout.settings_dialog, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        var voiceStatus = sharedPref.getBoolean("VOICE_STATUS", true)
        val voiceBtn = dialogView.findViewById<Button>(R.id.voiceSettingsButton)

        if (voiceStatus) {
            voiceBtn.text = "Включены"
        } else {
            voiceBtn.text = "Выключены"
        }

        voiceBtn.setOnClickListener {
            sharedPref.edit {
                putBoolean("VOICE_STATUS", !voiceStatus)
            }

            voiceStatus = !voiceStatus
            if (voiceStatus) {
                voiceBtn.text = "Включены"
            } else {
                voiceBtn.text = "Выключены"
            }
        }


        val clickerBtn = dialogView.findViewById<Button>(R.id.clickerSettingsButton)
        var clickerSkinStatus = sharedPref.getBoolean("CLICKER_CKIN", true)

        if (clickerSkinStatus) {
            clickerBtn.text = "По умолчанию"
        } else {
            clickerBtn.text = "MAX Gold"
        }

        clickerBtn.setOnClickListener {
            sharedPref.edit {
                putBoolean("CLICKER_CKIN", !clickerSkinStatus)
            }

            clickerSkinStatus = !clickerSkinStatus
            if (clickerSkinStatus) {
                clickerBtn.text = "По умолчанию"
            } else {
                clickerBtn.text = "MAX Gold"
            }

            val clickerButton = requireActivity().findViewById<ImageButton>(R.id.clickerButton)
            if (clickerSkinStatus) {
                clickerButton.setImageResource(R.drawable.max_coin)
            } else {
                clickerButton.setImageResource(R.drawable.max_coin2)
            }
        }


        dialog.show()
    }
}