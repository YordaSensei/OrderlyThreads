package com.example.orderlythreads

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Added Glide import
import com.example.orderlythreads.Database.Inventory

class InventoryAdapter(
    private var data: MutableList<Inventory>,
    private val onMenuClick: (item: Inventory, position: Int, anchorView: View) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    val items: MutableList<Inventory>
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
        holder.name.text = item.material
        holder.quantity.text = "Item Quantity: ${item.quantity}"

        if (!item.imageUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(item.imageUri))
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.img_placeholder)
        }

        holder.menu.setOnClickListener {
            onMenuClick(item, position, holder.menu)
        }
    }

    fun updateData(newData: List<Inventory>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
    fun addItem(newItem: Inventory) {
        data.add(newItem)
        notifyItemInserted(data.size - 1)
    }
}