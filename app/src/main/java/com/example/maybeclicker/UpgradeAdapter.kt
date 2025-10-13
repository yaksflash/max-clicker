package com.example.maybeclicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.databinding.ItemUpgradeBinding
import kotlin.math.pow
import com.example.maybeclicker.utils.formatCoinsExtended

class UpgradeAdapter(
    private val context: Context,
    private val upgradeList: List<Upgrade>,
    private val listener: OnUpgradeClickListener
) : RecyclerView.Adapter<UpgradeAdapter.UpgradeHolder>() {

    override fun getItemCount(): Int = upgradeList.size

    class UpgradeHolder(item: View, private val listener: OnUpgradeClickListener) :
        RecyclerView.ViewHolder(item) {
        val binding = ItemUpgradeBinding.bind(item)

        fun bind(upgrade: Upgrade, context: Context) = with(binding) {
            nameOfUpgrade.text = upgrade.name
            descriptionOfUpgrade.text = upgrade.description

            // пересчёт динамической цены
            val dynamicCost = getDynamicCost(upgrade.cost.toDouble(), upgrade.id, context)
            buttonOfUpgrade.text = formatCoinsExtended(dynamicCost.toDouble())

            buttonOfUpgrade.setOnClickListener {
                listener.onUpgradeClick(upgrade)
            }
        }


        private fun getDynamicCost(baseCost: Double, upgradeId: String, context: Context): Double {
            val sharedPrefs = context.getSharedPreferences("General", Context.MODE_PRIVATE)
            val account = sharedPrefs.getString("CURRENT_ACCOUNT", "!")
            val prefs = context.getSharedPreferences("${account}upgrades", Context.MODE_PRIVATE)
            val boughtCount = prefs.getInt(upgradeId, 0)

            val increment = baseCost / 2.toDouble()
            val multiplier = baseCost + increment * boughtCount * (boughtCount + 1) / 2
            return multiplier
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpgradeHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upgrade, parent, false)
        return UpgradeHolder(view, listener)
    }

    override fun onBindViewHolder(holder: UpgradeHolder, position: Int) {
        holder.bind(upgradeList[position], context)
    }

    interface OnUpgradeClickListener {
        fun onUpgradeClick(upgrade: Upgrade)
    }
}
