package com.example.maybeclicker

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import com.example.maybeclicker.utils.updateUI
import com.example.maybeclicker.utils.formatCoinsExtended
import com.example.maybeclicker.utils.showFloatingText
import com.example.maybeclicker.utils.moveButtonWithinRangeWithMargin
import androidx.core.content.edit
import com.example.maybeclicker.utils.setupLevelClick
import com.example.maybeclicker.utils.setupDailyRewardClick
import com.example.maybeclicker.utils.setupResearchesClick
import com.example.maybeclicker.utils.setupSettingsClick
import android.media.SoundPool
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import android.os.Handler
import android.os.Looper
import android.widget.Toast


class ClickerFragment : Fragment() {
    private lateinit var soundPool: SoundPool
    private var soundId: Int = 0
    private lateinit var adapter: DailyRewardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val isDark = prefs.getBoolean("isDarkTheme", false)

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clicker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref =
            requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)
        val sharedPrefGU =
            requireActivity().getSharedPreferences("${account}goldupgrades", Context.MODE_PRIVATE)

        var coinCount = sharedPref.getFloat("COIN_COUNT", 0f)

        val particleView = view.findViewById<ParticleView>(R.id.particleView)
        val clickerText = view.findViewById<TextView>(R.id.countOfClicks)
        val clickerButton = view.findViewById<ImageButton>(R.id.clickerButton)
        val parent = view.findViewById<FrameLayout>(R.id.rootLayout)
        val textFloating = view.findViewById<TextView>(R.id.textFloating)

        clickerText?.text = formatCoinsExtended(coinCount.toDouble())

        val clickerSkinStatus = sharedPref.getBoolean("CLICKER_CKIN", true)
        if (clickerSkinStatus) {
            clickerButton.setImageResource(R.drawable.max_coin)
        } else {
            clickerButton.setImageResource(R.drawable.max_coin2)
        }

        moveButtonWithinRangeWithMargin(clickerButton, parent)
        // Выполнить действие через 2 секунды (2000 мс)
        Handler(Looper.getMainLooper()).postDelayed({
            var coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
            clickerText?.text = formatCoinsExtended(coinCount.toDouble())
            updateUI()
        }, 3000)


        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder()
                    .setMaxStreams(10) // сколько одновременных звуков можно
                    .build()
        } else { SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0) }
        val clickSoundId = soundPool.load(requireContext(), R.raw.click_sound, 1)

        val textFloatingGold = view.findViewById<TextView>(R.id.textFloatingGold)

        // Клик по кнопке
        clickerButton.setOnClickListener {
            val voiceStatus = sharedPref.getBoolean("VOICE_STATUS", true)


            val clickerSkinStatus = sharedPref.getBoolean("CLICKER_CKIN", true)
            if (clickerSkinStatus) {
                clickerButton.setImageResource(R.drawable.max_coin)
            } else {
                clickerButton.setImageResource(R.drawable.max_coin2)
            }


            if (voiceStatus) {
                soundPool.play(clickSoundId, 1f, 1f, 0, 0, 1f)
            }
            var goldPower = sharedPrefGU.getInt("CLICK_POWER", 0)
            var clickPower = sharedPref.getInt("CLICK_POWER", 1)
            var coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
            var clicks = sharedPref.getInt("CLICKS_COUNT", 0)
            var coinMultiplier = sharedPref.getFloat("COIN_MULTIPLIER", 1f)

            val goldCoins = sharedPref.getInt("MAX_GOLD", 0)

            var randomValue = (0..999).random() == 0
            if (randomValue) {
                if (!clickerSkinStatus) {
                    sharedPref.edit {putInt("MAX_GOLD", goldCoins + 1)}
                    showFloatingText(textFloatingGold, "+GOLD <3")
                } else {
                    val clickBonus = clickPower * 1000
                    sharedPref.edit {putFloat("COIN_COUNT", (coinCount + clickBonus))}
                    showFloatingText(textFloatingGold, "+COINS x${clickBonus}")
                }

            }
            var coins = (clickPower.toFloat() + goldPower.toFloat()) * coinMultiplier

            showFloatingText(textFloating, "+${formatCoinsExtended(coins.toDouble())}")
            coinCount += coins
            clicks++
            clickerText?.text = formatCoinsExtended(coinCount.toDouble())

            sharedPref.edit {
                putFloat("COIN_COUNT", coinCount)
                putInt("CLICKS_COUNT", clicks)
                putFloat("COIN_MULTIPLIER", coinMultiplier)
                putInt("CLICK_POWER", clickPower)
            }

            updateUI()

            val x = it.x + it.width / 2f
            val y = it.y + it.height / 2f
            particleView.spawnParticles(x, y)
        }

        // Клик по уровню
        view.post {
            val levelLayout = requireActivity().findViewById<LinearLayout>(R.id.levelLayout)
            setupLevelClick(levelLayout)
            //var coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
            //updateUI()
            //clickerText?.text = formatCoinsExtended(coinCount.toDouble())


            val dailyRewardLayout = requireActivity().findViewById<LinearLayout>(R.id.dailyRewardsLayout)
            setupDailyRewardClick(dailyRewardLayout)

            val layoutOfIssledovaniya = requireActivity().findViewById<LinearLayout>(R.id.layoutOfIssledovaniya)
            setupResearchesClick(layoutOfIssledovaniya)

            val settingsLayout = requireActivity().findViewById<LinearLayout>(R.id.settingsLayout)
            setupSettingsClick(settingsLayout)
        }

    }

}
