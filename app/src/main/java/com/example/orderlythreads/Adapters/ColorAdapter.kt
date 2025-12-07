package com.example.orderlythreads

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlin.text.get

class ColorAdapter(
    private val context: Context,
    private val colors: List<Int>, // List of Color Integers (e.g. Color.RED)
    private val onColorSelected: (Int) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCircle: ImageView = itemView.findViewById(R.id.imgColorCircle)
        val imgSelection: ImageView = itemView.findViewById(R.id.imgSelectionRing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color_circle, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorInt = colors[position]

        // 1. Apply the color to the white circle
        holder.imgCircle.setColorFilter(colorInt, PorterDuff.Mode.SRC_IN)

        // 2. Handle Selection Visuals
        if (selectedPosition == position) {
            holder.imgSelection.visibility = View.VISIBLE
            holder.itemView.scaleX = 1.1f
            holder.itemView.scaleY = 1.1f
        } else {
            holder.imgSelection.visibility = View.GONE
            holder.itemView.scaleX = 1.0f
            holder.itemView.scaleY = 1.0f
        }

        // 3. Handle Clicks
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onColorSelected(colorInt)
        }
    }

    override fun getItemCount(): Int = colors.size

    // Helper to get the Hex string name for the confirmation dialog
    fun getSelectedHex(): String {
        if (selectedPosition == RecyclerView.NO_POSITION) return "None"
        val colorInt = colors[selectedPosition]
        return String.format("#%06X", (0xFFFFFF and colorInt))
    }
}
