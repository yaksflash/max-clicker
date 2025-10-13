package com.example.maybeclicker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.pow
import android.widget.Toast
import com.example.maybeclicker.utils.updateUI
import com.example.maybeclicker.utils.formatCoinsExtended
import androidx.recyclerview.widget.GridLayoutManager

class UpgradesFragment : Fragment() {

    private var clickPower = 0
    private var coinCount = 0f
    private var coinMultiplier = 0f
    private var offlineReward = 0

    private lateinit var adapter: UpgradeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updateUI()
        val view = inflater.inflate(R.layout.fragment_upgrades, container, false)

        // Загружаем апгрейды из JSON
        val upgrades = loadUpgradesFromAssets()

        // Достаём сохранённые значения
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)
        clickPower = sharedPref.getInt("CLICK_POWER", 1)
        coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
        coinMultiplier = sharedPref.getFloat("COIN_MULTIPLIER", 1f)
        offlineReward = sharedPref.getInt("OFFLINE_REWARD", 0)

        // Настройка RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.upgradesRecycler)

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)


        // Создаём адаптер
        adapter = UpgradeAdapter(requireContext(), upgrades, object : UpgradeAdapter.OnUpgradeClickListener {
            override fun onUpgradeClick(upgrade: Upgrade) {
                val sharedPrefs = requireContext().getSharedPreferences("General", Context.MODE_PRIVATE)
                val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")

                val prefs = requireContext().getSharedPreferences("${account.toString()}upgrades", Context.MODE_PRIVATE)
                val boughtCount = prefs.getInt(upgrade.id, 0)

                // Динамическая цена: экспоненциальный рост
                val increment = upgrade.cost.toDouble() / 2.toDouble()
                val dynamicCost = upgrade.cost.toDouble() + increment * boughtCount * (boughtCount + 1) / 2

                if (coinCount >= dynamicCost) {
                    coinCount = (coinCount.toDouble() - dynamicCost).toFloat()

                    when (upgrade.type) {
                        "ClickPower" -> clickPower += upgrade.value.toInt()
                        "OfflineReward" -> offlineReward += upgrade.value.toFloat().toInt()
                    }

                    // Увеличиваем количество купленных улучшений
                    prefs.edit { putInt(upgrade.id, boughtCount + 1) }

                    // Сохраняем новые значения
                    saveCounter(coinCount, coinMultiplier, clickPower, offlineReward.toInt())
                    updateUI()


                    // Обновляем только купленный элемент
                    val index = upgrades.indexOf(upgrade)
                    adapter.notifyItemChanged(index)
                } else {
                    Toast.makeText(context, "Вам недостает ~${formatCoinsExtended((dynamicCost - coinCount).toDouble())}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        recyclerView.adapter = adapter

        return view
    }




    private fun saveCounter(coins: Float, multiplier: Float, power: Int, offlineReward: Int) {
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)
        sharedPref.edit {
            putFloat("COIN_COUNT", coins)
            putFloat("COIN_MULTIPLIER", multiplier)
            putInt("CLICK_POWER", power)
            putInt("OFFLINE_REWARD", offlineReward)
        }
    }

    private fun loadUpgradesFromAssets(): List<Upgrade> {
        val inputStream = requireContext().assets.open("upgrades.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
        reader.close()

        val type = object : TypeToken<List<Upgrade>>() {}.type
        return Gson().fromJson(json, type)
    }
}
