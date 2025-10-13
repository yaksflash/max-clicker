package com.example.maybeclicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.maybeclicker.databinding.ItemResearchBinding
import androidx.core.content.ContextCompat
import androidx.core.content.edit

class ResearchesAdapter(
    private val context: Context,
    private val researches: List<Research>,
    private val listener: OnResearchesClickListener
) : RecyclerView.Adapter<ResearchesAdapter.ResearchesHolder>() {

    override fun getItemCount(): Int = researches.size

    class ResearchesHolder(item: View, private val listener: OnResearchesClickListener) :
        RecyclerView.ViewHolder(item) {
        val binding = ItemResearchBinding.bind(item)

        fun bind(research: Research, context: Context) = with(binding) {
            nameOfResearch.text=research.name
            buttonOfResearch.text=research.price.toString()

            // обработчик нажатия
            buttonOfResearch.setOnClickListener {
                listener.OnResearchesClick(research)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchesHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_research, parent, false)
        return ResearchesHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ResearchesHolder, position: Int) {
        holder.bind(researches[position], context)
    }

    interface OnResearchesClickListener {
        fun OnResearchesClick(research: Research)
    }
}
