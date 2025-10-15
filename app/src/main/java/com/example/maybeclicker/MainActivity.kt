package com.example.maybeclicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.content.Context
import androidx.core.content.edit
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.maybeclicker.utils.formatCoinsExtended
import com.example.maybeclicker.utils.updateUI

import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController



import androidx.core.content.edit

import com.example.maybeclicker.utils.formatCoinsExtended
import com.example.maybeclicker.utils.updateUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val config = resources.configuration
        if (config.fontScale != 1f) {
            config.fontScale = 1f
            applyOverrideConfiguration(config)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Однократное обновление UI при старте
        updateUI(this)

        val navController = findNavController(R.id.fragmentContainerView)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val sharedPrefs = this.getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = this.getSharedPreferences(account, Context.MODE_PRIVATE)
        val sharedPrefGU = this.getSharedPreferences("${account}goldupgrades", Context.MODE_PRIVATE)

        val rewardShown = sharedPref.getBoolean("DIALOG_SHOWN", false)

        if (!rewardShown) {
            sharedPref.edit { putBoolean("DIALOG_SHOWN", true) }

            val dialogView = LayoutInflater.from(this).inflate(R.layout.offline_reward_dialog, null)
            val builder = AlertDialog.Builder(this).setView(dialogView)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

            val lastExitTime = sharedPref.getLong("LAST_EXIT_TIME", System.currentTimeMillis())
            val rewardPerMinute = sharedPref.getInt("OFFLINE_REWARD", 0)
            val rewardMultiplierGold = sharedPrefGU.getFloat("OFFLINE_MULTIPLIER", 1f)
            val coins = sharedPref.getFloat("COIN_COUNT", 0f)

            if (lastExitTime == 0L || System.currentTimeMillis() - lastExitTime < 60000) {
                sharedPref.edit { putLong("LAST_EXIT_TIME", System.currentTimeMillis()) }
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val rewardText = dialogView.findViewById<TextView>(R.id.rewardText)
                    val offlineTimeText = dialogView.findViewById<TextView>(R.id.offlineTimeText)

                    val currentTimeMillis = System.currentTimeMillis()
                    var offlineMinutes = ((currentTimeMillis - lastExitTime) / 1000 / 60).toInt()
                    if (offlineMinutes >= 2 * 60) offlineMinutes = 120

                    val reward = if (offlineMinutes >= 10 && rewardPerMinute > 0)
                        offlineMinutes * (rewardPerMinute * rewardMultiplierGold)
                    else 0

                    sharedPref.edit { putFloat("COIN_COUNT", coins + reward.toFloat()) }

                    rewardText.text = "$reward (+x$rewardMultiplierGold)"
                    offlineTimeText.text = "Offline minutes: $offlineMinutes"



                    val closeButton = dialogView.findViewById<Button>(R.id.closeRewardBtn)
                    closeButton?.setOnClickListener {
                        if (dialog.isShowing) dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        sharedPref.edit { putLong("LAST_EXIT_TIME", System.currentTimeMillis()) }

                    }

                    dialog.show()
                }, 750)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val sharedPrefs = getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!") ?: "!"
        val sharedPref = getSharedPreferences(account, Context.MODE_PRIVATE)
        sharedPref.edit {
            putLong("LAST_EXIT_TIME", System.currentTimeMillis())
            putBoolean("DIALOG_SHOWN", false)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val config = newBase.resources.configuration
        if (config.fontScale != 1f) config.fontScale = 1f
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }


}

