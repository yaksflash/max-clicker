package com.example.maybeclicker.utils

import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.DailyReward
import com.example.maybeclicker.DailyRewardAdapter
import com.example.maybeclicker.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader


fun Fragment.setupDailyRewardClick(dailyRewardLayout: LinearLayout?) {

    // Загружаем награды из assets
    fun loadDailyRewardsFromAssets(): List<DailyReward> {
        val inputStream = requireContext().assets.open("daily_reward.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
        reader.close()

        val type = object : TypeToken<List<DailyReward>>() {}.type
        return Gson().fromJson(json, type)
    }

    dailyRewardLayout?.setOnClickListener {
        updateUI()
        // Подготавливаем окно
        val dialogView = layoutInflater.inflate(R.layout.dailyreward_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        // Настройки аккаунта
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

        // Настройка списка наград
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.dailyRewardsRecyclerView)
        val dailyRewards = loadDailyRewardsFromAssets()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        lateinit var adapter: DailyRewardAdapter

        adapter = DailyRewardAdapter(requireContext(), dailyRewards, object : DailyRewardAdapter.OnDailyRewardClickListener {
            override fun OnDailyRewardClick(claimReward: DailyReward) {
                val lastRewardTime = sharedPref.getLong("LAST_DAILY_REWARD_TIME", 15L)
                val dailyRewardStreak = sharedPref.getInt("DAILY_REWARD_STREAK", 0)

                if (lastRewardTime == 15L) {
                    sharedPref.edit {
                        putLong("LAST_DAILY_REWARD_TIME", System.currentTimeMillis())
                        Toast.makeText(context, "Возвращайтесь через 24 часа :)", Toast.LENGTH_LONG).show()
                    }
                    return
                }
                val oneDayMillis = 24 * 60 * 60 * 1000L // миллисекунд в сутках
                //val oneDayMillis = 10 * 1000L // миллисекунд в сутках
                val elapsed = System.currentTimeMillis() - lastRewardTime

                // Проверка последовательности (чтобы нельзя было пропускать дни)
                if (claimReward.day > dailyRewardStreak + 1) {
                    Toast.makeText(context, "Сначала заберите предыдущую награду", Toast.LENGTH_LONG).show()
                    return
                }

                when {
                    // Прошло более 2 дней — сброс
                    elapsed > oneDayMillis * 2 -> {
                        sharedPref.edit {
                            putLong("LAST_DAILY_REWARD_TIME", System.currentTimeMillis())
                            putInt("DAILY_REWARD_STREAK", 0)
                        }
                        dialog.dismiss()
                        Toast.makeText(context, "Награды сброшены, надо было заходить ежедневно", Toast.LENGTH_LONG).show()
                    }

                    // Прошёл хотя бы 1 день — можно забрать награду
                    elapsed >= oneDayMillis -> {
                        sharedPref.edit {
                            putInt("DAILY_REWARD_STREAK", dailyRewardStreak + 1)
                            putLong("LAST_DAILY_REWARD_TIME", System.currentTimeMillis())
                        }

                        val userCoins = sharedPref.getFloat("COIN_COUNT", 0f)
                        val userGoldCoins = sharedPref.getInt("MAX_GOLD", 0)


                        val reward = claimReward.value
                        when (claimReward.reward) {
                            "coin" -> sharedPref.edit {
                                putFloat("COIN_COUNT", userCoins + reward)
                            }
                            "gold" -> sharedPref.edit {
                                putInt("MAX_GOLD", userGoldCoins + reward)
                            }
                        }
                        if (dailyRewards.size == claimReward.day) {
                            sharedPref.edit {
                                putInt("DAILY_REWARD_STREAK", 0)
                                Toast.makeText(context, "Вы забрали все награды, сброс!", Toast.LENGTH_LONG).show()
                            }
                        }
                        dialog.dismiss()

                        Toast.makeText(context, "Награда получена!", Toast.LENGTH_SHORT).show()
                    }

                    // Ещё не прошло 24 часа — считаем оставшееся время
                    else -> {
                        val remaining = oneDayMillis - elapsed
                        val hours = remaining / (1000 * 60 * 60)
                        val minutes = (remaining / (1000 * 60)) % 60
                        val seconds = (remaining / 1000) % 60
                        val formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        Toast.makeText(context, "Подождите: $formatted!", Toast.LENGTH_LONG).show()
                    }
                }

            }
        })
        recyclerView.adapter = adapter
        dialog.show()
    }
}
