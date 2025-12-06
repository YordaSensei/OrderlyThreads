package com.example.orderlythreads.Adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Models.DesignOption
import com.example.orderlythreads.R

class DesignOptionAdapter(
    private val items: List<DesignOption>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<DesignOptionAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
        val textView: TextView = itemView.findViewById(R.id.itemText)
        val borderView: View = itemView.findViewById(R.id.selectionBorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_design_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.textView.text = item.name

        // If imageResId is valid, use it. Otherwise, use a gray background (for testing)
        if (item.imageResId != 0) {
            holder.imageView.setImageResource(item.imageResId)
            holder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE // Or CENTER_CROP depending on look
        } else {
            holder.imageView.setBackgroundColor(Color.LTGRAY)
        }

        // Handle Selection Visuals
        if (selectedPosition == position) {
            holder.borderView.visibility = View.VISIBLE
            holder.itemView.alpha = 1.0f
        } else {
            holder.borderView.visibility = View.GONE
            holder.itemView.alpha = 0.5f
        }

        // Handle Clicks
        holder.itemView.setOnClickListener {
            val previousItem = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousItem)
            notifyItemChanged(selectedPosition)
            onItemSelected(item.name)
        }
    }

    override fun getItemCount() = items.size
}
