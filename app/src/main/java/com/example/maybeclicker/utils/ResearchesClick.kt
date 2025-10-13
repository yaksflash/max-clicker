package com.example.maybeclicker.utils

import android.content.Context
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.content.edit
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.Research
import com.example.maybeclicker.ResearchesAdapter
import com.example.maybeclicker.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

fun Fragment.setupResearchesClick(researchesLayout: LinearLayout?) {
    // Загружаем награды из assets
    fun loadResearchesFromAssets(): List<Research> {
        val inputStream = requireContext().assets.open("researches.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.readText()
        reader.close()

        val type = object : TypeToken<List<Research>>() {}.type
        return Gson().fromJson(json, type)
    }

    researchesLayout?.setOnClickListener {
        // Подготавливаем окно
        val dialogView = layoutInflater.inflate(R.layout.researches_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        // Настройки аккаунта
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref =
            requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)
        val sharedPrefGU =
            requireActivity().getSharedPreferences("${account}goldupgrades", Context.MODE_PRIVATE)

        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.researchesRecyclerView)
        val researches = loadResearchesFromAssets()
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        lateinit var adapter: ResearchesAdapter

        adapter = ResearchesAdapter(
            requireContext(),
            researches,
            object : ResearchesAdapter.OnResearchesClickListener {
                override fun OnResearchesClick(research: Research) {
                    val goldClickPower = sharedPrefGU.getInt("CLICK_POWER", 0)
                    val goldOfflineMultiplier = sharedPrefGU.getFloat("OFFLINE_MULTIPLIER", 1F)

                    val userGold = sharedPref.getInt("MAX_GOLD", 0)
                    val price = research.price
                    val value = research.value
                    val type = research.type
                    if (userGold >= price) {
                        sharedPref.edit {
                            putInt("MAX_GOLD", userGold - price)
                        }
                        when {
                            type == "clickPower" -> {
                                sharedPrefGU.edit {
                                    putInt("CLICK_POWER", goldClickPower + value.toInt())
                                }
                            }
                            type == "offlineBonus" -> {
                                sharedPrefGU.edit {
                                    putFloat("OFFLINE_MULTIPLIER", goldOfflineMultiplier + value)
                                }
                            }
                        }
                        updateUI()
                        Toast.makeText(context, "Успешный апгрейд!", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, "Недостаточно GOLD-коинов!", Toast.LENGTH_SHORT).show()
                    }

                }
            })
        recyclerView.adapter = adapter
        dialog.show()
    }
}
