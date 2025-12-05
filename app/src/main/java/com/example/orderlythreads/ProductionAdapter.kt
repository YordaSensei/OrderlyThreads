package com.example.orderlythreads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductionAdapter (private val list: List<ProductionClassItems>) : RecyclerView.Adapter<ProductionAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.production_item, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(item.image)

        // sets the text to the textview from our itemHolder class
        holder.clientName.text = item.clientName
        holder.schedule.text = item.date
        holder.status.text = item.status

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = itemView.findViewById(R.id.garmentContainer)
        val clientName: TextView = itemView.findViewById(R.id.clientName)
        val schedule: TextView = itemView.findViewById(R.id.schedule)
        val status: TextView = itemView.findViewById(R.id.status)
    }
}