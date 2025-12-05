package com.example.orderlythreads

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt

class ImageAdapter(
    private val context: Context,
    private val imageList: List<Int>,
    private val layoutResId: Int,
    private val isBrowsable: Boolean = false,
    private val isGridMode: Boolean = false,
    private val onItemClickCallback: (Int) -> Unit = {}
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView? = view.findViewById(R.id.imgGarment)
            ?: view.findViewById(R.id.imgColor)
            ?: view.findViewById(R.id.imgFabric)
            ?: view.findViewById(R.id.imgDesign)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imgView = holder.imageView ?: return
        val cardView = holder.itemView as? CardView
        val density = context.resources.displayMetrics.density


        val params = holder.itemView.layoutParams
        if (isGridMode) {
            val size = (80 * density).toInt()
            params.width = size
            params.height = size

            if (params is ViewGroup.MarginLayoutParams) {
                val margin = (4 * density).toInt()
                params.setMargins(margin, margin, margin, margin)
            }
        }
        holder.itemView.layoutParams = params

        imgView.setImageResource(imageList[position])

        val isSearchItem = position == 0 && imageList.getOrNull(0) == R.drawable.ic_search && (isBrowsable || isGridMode)
        if (isSearchItem) {
            if (cardView != null) {
                cardView.setCardBackgroundColor("#f6f6f6".toColorInt())
                val w = params.width.takeIf { it > 0 } ?: (80 * density).toInt()
                cardView.radius = w / 2f
            }

            imgView.setColorFilter("#444234".toColorInt())

            val padding = (12 * density).toInt()
            imgView.setPadding(padding, padding, padding, padding)

            holder.itemView.alpha = 1.0f
            holder.itemView.scaleX = 1.0f
            holder.itemView.scaleY = 1.0f

        } else {
            if (cardView != null) {
                cardView.setCardBackgroundColor(Color.WHITE)
                cardView.cardElevation = 2 * density

                if (isGridMode || layoutResId == R.layout.item_color_circle || layoutResId == R.layout.item_fabric_card) {
                    val w = params.width.takeIf { it > 0 } ?: (80 * density).toInt()
                    cardView.radius = w / 2f
                } else {
                    cardView.radius = 12 * density
                }
            }

            imgView.clearColorFilter()
            imgView.setPadding(0, 0, 0, 0)

            if (position == selectedPosition) {
                holder.itemView.alpha = 0.6f
            } else {
                holder.itemView.alpha = 1.0f
            }

        }

        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener

            onItemClickCallback(currentPos)
        }
    }

    override fun getItemCount(): Int = imageList.size
    fun getSelectionName(): String {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            "Item at position ${selectedPosition + 1}"
        } else {
            "None Selected"
        }
    }
    fun setSelection(position: Int) {
        if (position == selectedPosition) return

        val previousPos = selectedPosition
        selectedPosition = position

        if (previousPos != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPos)
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition)
        }
    }
    fun clearSelection() {
        if (selectedPosition == RecyclerView.NO_POSITION) return

        val previousPos = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previousPos)
    }
}
