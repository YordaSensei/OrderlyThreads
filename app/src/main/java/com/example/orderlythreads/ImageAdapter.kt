package com.example.orderlythreads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(
    private val context: Context,
    private val imageList: List<Int>,
    private val layoutResId: Int,
    private val onItemClickCallback: () -> Unit = {}
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.imgGarment)
            ?: view.findViewById(R.id.imgColor)
            ?: view.findViewById(R.id.imgFabric)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView?.setImageResource(imageList[position])

        if (position == selectedPosition) {
            holder.itemView.alpha = 0.5f
        } else {
            holder.itemView.alpha = 1.0f
        }

        holder.itemView.scaleX = 1.0f
        holder.itemView.scaleY = 1.0f

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION && selectedPosition != currentPos) {
                val previousPos = selectedPosition
                selectedPosition = currentPos

                if (previousPos != RecyclerView.NO_POSITION) notifyItemChanged(previousPos)
                notifyItemChanged(selectedPosition)

                onItemClickCallback()
            }
        }
    }

    override fun getItemCount(): Int = imageList.size

    fun getSelectionName(): String {
        return if (selectedPosition != RecyclerView.NO_POSITION) "Option ${selectedPosition + 1}" else "None"
    }

    fun clearSelection() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            val previousPos = selectedPosition
            selectedPosition = RecyclerView.NO_POSITION
            notifyItemChanged(previousPos)
        }
    }
}