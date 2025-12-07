package com.example.orderlythreads

import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderlythreads.Database.Inventory

class FabricSelectionAdapter(
    private val onItemClick: (Inventory) -> Unit
) : RecyclerView.Adapter<FabricSelectionAdapter.ViewHolder>() {

    private var items = listOf<Inventory>()
    var selectedPosition = RecyclerView.NO_POSITION // Track selection

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cardImage)
        val name: TextView = view.findViewById(R.id.cardName)
        val quantity: TextView = view.findViewById(R.id.cardQuantity)
        val menu: ImageView = view.findViewById(R.id.cardMenu)
        val cardContainer: View = view // The whole card
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Reuse your existing layout!
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.name.text = item.material
        holder.quantity.text = "Qty: ${item.quantity}"

        // Hide the menu button since we are just selecting
        holder.menu.visibility = View.GONE

        // Load Image
        if (!item.imageUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(item.imageUri))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.img_placeholder)
        }

        // --- HANDLE SELECTION VISUALS ---
        if (selectedPosition == position) {
            // Visual feedback for selected item (e.g., Red Border or Darker Background)
            holder.itemView.setBackgroundResource(R.drawable.rounded_bg_border_rectangular_only) // Using the red ring file we made
        } else {
            holder.itemView.background = null
        }

        // Click Logic
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition

            // Refresh to update visuals
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            onItemClick(item)
        }
    }

    fun setData(newItems: List<Inventory>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    // Helper to get selected ID for saving to database
    fun getSelectedId(): Int {
        if (selectedPosition != RecyclerView.NO_POSITION && items.isNotEmpty()) {
            return items[selectedPosition].id
        }
        return 0 // or -1
    }

    // Helper to get selected Name for the summary
    fun getSelectedName(): String {
        if (selectedPosition != RecyclerView.NO_POSITION && items.isNotEmpty()) {
            return items[selectedPosition].material
        }
        return "None"
    }
}