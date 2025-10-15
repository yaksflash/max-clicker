package com.example.maybeclicker

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import com.example.maybeclicker.utils.updateUI
import com.example.maybeclicker.utils.formatCoinsExtended
import android.content.Intent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateUI()
        val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
        val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
        val sharedPref = requireActivity().getSharedPreferences(account.toString(), Context.MODE_PRIVATE)
        val sharedPrefGU =
            requireActivity().getSharedPreferences("${account}goldupgrades", Context.MODE_PRIVATE)

        val clickPower = sharedPref.getInt("CLICK_POWER", 1)
        val coinCount = sharedPref.getFloat("COIN_COUNT", 0f)
        val userMultiplier = sharedPref.getFloat("COIN_MULTIPLIER", 1f)
        val cliksCount = sharedPref.getInt("CLICKS_COUNT", 0)
        val offlineReward = sharedPref.getInt("OFFLINE_REWARD", 0)
        val maxGold = sharedPref.getInt("MAX_GOLD", 0)

        var goldPower = sharedPrefGU.getInt("CLICK_POWER", 0)
        val rewardMultiplierGold = sharedPrefGU.getFloat("OFFLINE_MULTIPLIER", 1f)

        // Находим TextView на Activity
        val cliks = requireActivity().findViewById<TextView>(R.id.clicksCount)
        val power = requireActivity().findViewById<TextView>(R.id.clickPower)
        val multiplier = requireActivity().findViewById<TextView>(R.id.coinMultiplier)
        val coins = requireActivity().findViewById<TextView>(R.id.coinsCount)
        val offline = requireActivity().findViewById<TextView>(R.id.offlineRewardProfileText)
        val gold = requireActivity().findViewById<TextView>(R.id.maxGoldCount)

        val accName = requireActivity().findViewById<TextView>(R.id.statsAccName)
        accName.text = account

        cliks.text = cliksCount.toString()
        power.text = "$clickPower + $goldPower"
        multiplier.text = userMultiplier.toString()
        coins.text = formatCoinsExtended(coinCount.toDouble()).toString()
        offline.text = "$offlineReward * $rewardMultiplierGold"
        gold.text = maxGold.toString()

        val leaveAccBtn = requireActivity().findViewById<Button>(R.id.leaveAccButton)
        leaveAccBtn.setOnClickListener {
            sharedPrefs.edit {
                putString("CURRENT_ACCOUNT", "!")
            }
            val intent = Intent(requireActivity(), RegistrationWindow::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        val deleteAccBtn = requireActivity().findViewById<Button>(R.id.deleteAccButton)
        deleteAccBtn.setOnClickListener {
            val sharedPrefs = requireActivity().getSharedPreferences("General", Context.MODE_PRIVATE)
            val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")


            val success = requireActivity().deleteSharedPreferences(account) &&
                    requireActivity().deleteSharedPreferences("${account}upgrades")
                            && requireActivity().deleteSharedPreferences("${account}goldupgrades")

            if (success) {
                Toast.makeText(context, "$account удалён", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Не удалось удалить $account", Toast.LENGTH_SHORT).show()
            }

            val str = "${(sharedPrefs.getString("ACCOUNTS", "!"))}"

            var result = str.split("|")          // ["", "name1", "name2", "name", ""]
                .filter { it.isNotEmpty() && it != account } // убираем пустые и name2
                .joinToString("|", prefix = "|", postfix = "|")
                .drop(1).dropLast(1)

            if (result == "")
                result = "!"


            sharedPrefs.edit {
                putString("CURRENT_ACCOUNT", "!")
                putString("ACCOUNTS", result)
            }

            val intent = Intent(requireActivity(), RegistrationWindow::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}