package com.example.orderlythreads

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView // Import if your card has a text label
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderlythreads.Database.Inventory

class AccentSelectionAdapter(
    private val onItemClick: (Inventory) -> Unit
) : RecyclerView.Adapter<AccentSelectionAdapter.ViewHolder>() {

    private var items = listOf<Inventory>()
    var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // IMPORTANT: Check your item_garment_card.xml IDs!
        // I am assuming the ImageView ID is 'imgGarment' or 'imageView'.
        // Update R.id.imgGarment to whatever is inside item_garment_card.xml
        val image: ImageView = view.findViewById(R.id.imgGarment)

        // If your card has a text view for the name, uncomment below:
        // val name: TextView = view.findViewById(R.id.tvGarmentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Here we use the correct layout: item_garment_card
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_garment_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 1. LOAD IMAGE WITH CIRCLE CROP
        if (!item.imageUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(item.imageUri))
                .circleCrop() // <--- This forces the image into a circle
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(holder.image)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.img_placeholder)
                .circleCrop() // <--- Circle crop the placeholder too
                .into(holder.image)
        }

        // 2. HANDLE SELECTION (RED RING)
        if (selectedPosition == position) {
            // We apply the ring to the background of the image (or the parent view)
            holder.image.setBackgroundResource(R.drawable.rounded_bg_border_only)

            // If the image has padding, the border might look better on the itemView:
            // holder.itemView.setBackgroundResource(R.drawable.rounded_bg_border_only)
        } else {
            holder.image.background = null
            // holder.itemView.background = null
        }

        // 3. CLICK LISTENER
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onItemClick(item)
        }
    }

    fun setData(newItems: List<Inventory>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun getSelectedId(): Int {
        if (selectedPosition != RecyclerView.NO_POSITION && items.isNotEmpty()) {
            return items[selectedPosition].id
        }
        return 0
    }

    fun getSelectedName(): String {
        if (selectedPosition != RecyclerView.NO_POSITION && items.isNotEmpty()) {
            return items[selectedPosition].material
        }
        return "None"
    }
}
