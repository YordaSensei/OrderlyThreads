package com.example.orderlythreads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class InventoryItem(val name: String, val quantity: String, val imageRes: Int)

class InventoryAdapter(
    private var data: MutableList<InventoryItem>,
    private val onMenuClick: (item: InventoryItem, position: Int, anchorView: View) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    val items: MutableList<InventoryItem>
        get() = data

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.cardImage)
        val name = view.findViewById<TextView>(R.id.cardName)
        val quantity = view.findViewById<TextView>(R.id.cardQuantity)
        val menu = view.findViewById<ImageView>(R.id.cardMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.name.text = item.name
        holder.quantity.text = "Item Quantity: ${item.quantity}"
        holder.image.setImageResource(item.imageRes)
        holder.menu.setOnClickListener {
            onMenuClick(item, position, holder.menu)
        }
    }

    fun updateData(newData: MutableList<InventoryItem>) {
        data = newData
        notifyDataSetChanged() // Keep for tab switching
    }
}