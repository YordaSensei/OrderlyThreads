package com.example.orderlythreads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// MODEL
data class OrderItem(
    val name: String,
    val date: String,
    val label: String,
    var status: String,
    val imageRes: Int
)

class OrdersAdapter(
    private var data: MutableList<OrderItem>,
    private val onVerifyClick: (item: OrderItem, position: Int) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.orderShirtImage)
        val name = view.findViewById<TextView>(R.id.orderName)
        val date = view.findViewById<TextView>(R.id.orderDateTime)
        val label = view.findViewById<TextView>(R.id.orderLabel)
        val status = view.findViewById<TextView>(R.id.orderStatus)
        val verifyButton = view.findViewById<TextView>(R.id.verifyStockButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_stock_check_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.image.setImageResource(item.imageRes)
        holder.name.text = item.name
        holder.date.text = item.date
        holder.label.text = item.label
        holder.status.text = item.status

        // Set background color based on status
        if (item.status == "Approved") {
            holder.status.background.setTint(holder.status.context.getColor(android.R.color.holo_green_light))
        } else {
            holder.status.background.setTint(holder.status.context.getColor(android.R.color.holo_orange_light))
        }

        // Click listener for Verify Stock button
        holder.verifyButton.setOnClickListener {
            onVerifyClick(item, position)
        }
    }

    fun updateData(newData: MutableList<OrderItem>) {
        data = newData
        notifyDataSetChanged()
    }
}
