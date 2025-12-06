package com.example.orderlythreads.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Models.DesignOption
import com.example.orderlythreads.R

class DesignOptionAdapter(
    private val options: List<DesignOption>,
    private val onItemSelected: (String) -> Unit
) : RecyclerView.Adapter<DesignOptionAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    // The ViewHolder holds the views for a single item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // This is where the "Unresolved reference 'itemImage'" error comes from.
        // We need an ImageView with the ID 'itemImage' in our layout file.
        val imageView: ImageView = itemView.findViewById(R.id.itemImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for a single item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_design_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]

        // Set the image for the current item
        holder.imageView.setImageResource(option.imageResId)

        // Handle item selection visual state
        holder.itemView.alpha = if (position == selectedPosition) 0.5f else 1.0f

        // Handle clicks
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            // Callback to inform the activity of the selection
            onItemSelected(option.name)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }
}
