package com.example.maybeclicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.databinding.ItemDailyrewardBinding
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class DailyRewardAdapter(
    private val context: Context,
    private val rewardList: List<DailyReward>,
    private val listener: OnDailyRewardClickListener
) : RecyclerView.Adapter<DailyRewardAdapter.DailyRewardHolder>() {

    override fun getItemCount(): Int = rewardList.size

    class DailyRewardHolder(item: View, private val listener: OnDailyRewardClickListener) :
        RecyclerView.ViewHolder(item) {
        val binding = ItemDailyrewardBinding.bind(item)

        fun bind(claimReward: DailyReward, context: Context) = with(binding) {
            nameBonus.text = claimReward.name
            countOfBonus.text = claimReward.value.toString()
            nameOfDay.text = "Day #${claimReward.day}"

            val sharedPrefs = context.getSharedPreferences("General", Context.MODE_PRIVATE)
            val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
            val sharedPref = context.getSharedPreferences(account.toString(), Context.MODE_PRIVATE)

            var dailyRewardStreak = sharedPref.getInt("DAILY_REWARD_STREAK", 0)
            var lastRewardTime = sharedPref.getLong("LAST_DAILY_REWARD_TIME", System.currentTimeMillis())

            val oneDayMillis = 24 * 60 * 60 * 1000L // 1 день в миллисекундах

            // Если прошло 2 дня — сброс streak
            if (lastRewardTime != 0L && System.currentTimeMillis() - lastRewardTime > oneDayMillis * 2) {
                dailyRewardStreak = 0
                sharedPref.edit {
                    putInt("DAILY_REWARD_STREAK", 0)
                }
            }


            // Определяем цвет и кликабельность
            when {
                // ✅ Уже полученные
                claimReward.day <= dailyRewardStreak -> {
                    rewardLayout.background = ContextCompat.getDrawable(context, R.drawable.green_transparent_bg)
                    rewardLayout.isEnabled = false
                    rewardLayout.isClickable = false
                    rewardLayout.isFocusable = false
                    nameOfDay.setTextColor(ContextCompat.getColor(context, R.color.black))
                    nameBonus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    countOfBonus.setTextColor(ContextCompat.getColor(context, R.color.black))

                }

                // ⭐ Доступная сейчас награда
                claimReward.day == dailyRewardStreak + 1 -> {
                    rewardLayout.background = ContextCompat.getDrawable(context, R.drawable.yellow_transparent_bg)
                    nameOfDay.setTextColor(ContextCompat.getColor(context, R.color.black))
                    nameBonus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    countOfBonus.setTextColor(ContextCompat.getColor(context, R.color.black))
                    rewardLayout.setOnClickListener {
                        listener.OnDailyRewardClick(claimReward)
                    }
                }
                // ⛔ Недоступные
                else -> {
                    rewardLayout.isEnabled = false
                    rewardLayout.isClickable = false
                    rewardLayout.isFocusable = false
                }
            }





        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyRewardHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dailyreward, parent, false)
        return DailyRewardHolder(view, listener)
    }

    override fun onBindViewHolder(holder: DailyRewardHolder, position: Int) {
        holder.bind(rewardList[position], context)
    }

    interface OnDailyRewardClickListener {
        fun OnDailyRewardClick(claimReward: DailyReward)
    }
}
