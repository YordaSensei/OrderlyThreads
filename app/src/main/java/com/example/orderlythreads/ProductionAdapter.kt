package com.example.orderlythreads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.TypedValue

data class ProductionItems(
    val id: Int,
    val image: Int,
    val clientName: String,
    val date: String,
    val label: String,
    val status: String
)

class ProductionAdapter ( //list of items and track button function when clicked
    private var list: MutableList<ProductionItems>,
    private val onTrackBtnClick: (item: ProductionItems, position: Int) -> Unit) : RecyclerView.Adapter<ProductionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = itemView.findViewById(R.id.garmentContainer)
        val clientName: TextView = itemView.findViewById(R.id.clientName)
        val schedule: TextView = itemView.findViewById(R.id.schedule)
        val label: TextView = itemView.findViewById(R.id.label)
        val status: TextView = itemView.findViewById(R.id.status)
        val trackOrdeBtn: TextView = itemView.findViewById(R.id.trackOrderBtn)
        val itemRectangle: View = itemView.findViewById(R.id.itemRectangle)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the view that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.production_item, parent, false)

        return ViewHolder(view)
    }

    // gets the number of items in the list
    override fun getItemCount(): Int = list.size

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(item.image)
        holder.clientName.text = item.clientName
        holder.schedule.text = item.date
        holder.label.text = item.label
        holder.status.text = item.status

        if (item.status == "Cutting") {
            holder.status.setBackgroundResource(R.drawable.yellow_background)
        } else if (item.status == "Sewing") {
            holder.status.setBackgroundResource(R.drawable.orange_background)
        } else if (item.status == "Completed") {
            holder.status.setBackgroundResource(R.drawable.green_background)
        } else  {
            holder.status.setBackgroundResource(R.drawable.red_background)
        }

        if (item.status == "Completed") {
            holder.trackOrdeBtn.visibility = View.GONE
        } else {
            holder.trackOrdeBtn.visibility = View.VISIBLE
        }

        holder.itemView.post {
            // This code runs after the initial layout, so we can now safely get and set dimensions.
            val itemRectangleParams = holder.itemRectangle.layoutParams
            // Get the screen's display metrics to convert dp to px
            val displayMetrics = holder.itemView.context.resources.displayMetrics

            if (item.status == "Completed") {
                itemRectangleParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 135f, displayMetrics
                ).toInt() // Convert Float result to Int

                holder.status.textSize = 11f // This is correct (Float)
            } else {
                itemRectangleParams.height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 165f, displayMetrics
                ).toInt() // Convert Float result to Int

                holder.status.textSize = 13f
            }
            // Apply the changed parameters to the view.
            holder.itemRectangle.layoutParams = itemRectangleParams
        }

        holder.trackOrdeBtn.setOnClickListener {
            onTrackBtnClick(item, position)
        }
    }

    // updates the data when tabs are switched
    fun updateView (newTab : MutableList<ProductionItems>) {
        list = newTab
        notifyDataSetChanged() // calls recycler view to refresh new data
    }
}