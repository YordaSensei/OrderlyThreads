package com.example.orderlythreads

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.drawable.toDrawable

class SelectSampleDesign : AppCompatActivity() {

    data class DesignItem(
        val imageRes: Int,
        val title: String,
        val description: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_sample_design)

        val btnMakeOrder = findViewById<Button>(R.id.makeOrderBtn)
        btnMakeOrder.setOnClickListener {
            val intent = Intent(this, SelectDesignAttributes::class.java)
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rvSampleDesigns)

        val designs = listOf(
            DesignItem(R.drawable.custom_order_1, "Bloody Mary Gown", "An Elegant red evening gown."),
            DesignItem(R.drawable.custom_order_2, "Smoothz Dress", "A Modern silk dress."),
            DesignItem(R.drawable.custom_order_3, "Classic Chap", "A Classic vintage style."),
            DesignItem(R.drawable.custom_order_4, "Florescent", "A Summer floral pattern."),
            DesignItem(R.drawable.custom_order_5, "Classic Suit", "A Professional wear."),
            DesignItem(R.drawable.custom_order_6, "Slizzy Dress", "A Casual slick outfit."),
        )

        val adapter = SampleDesignAdapter(designs) { selectedDesign ->
            showDesignDialog(selectedDesign)
        }


        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter
    }

    private fun showDesignDialog(design: DesignItem) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_design_details)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val img = dialog.findViewById<ImageView>(R.id.dialogImgDesign)
        val title = dialog.findViewById<TextView>(R.id.dialogTvTitle)
        val desc = dialog.findViewById<TextView>(R.id.dialogTvDescription)
        val btnClose = dialog.findViewById<ImageButton>(R.id.btnCloseDialog)

        img.setImageResource(design.imageRes)
        title.text = design.title
        desc.text = design.description

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    class SampleDesignAdapter(
        private val items: List<DesignItem>,
        private val onItemClick: (DesignItem) -> Unit
    ) : RecyclerView.Adapter<SampleDesignAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.imgDesign)
            val title: TextView = view.findViewById(R.id.tvDesignTitle)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sample_design, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.img.setImageResource(item.imageRes)
            holder.title.text = item.title

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        override fun getItemCount() = items.size
    }
//    val logoutBtn = findViewById<ImageButton>(R.id.logOutBtn)
//
//    logoutBtn.setOnClickListener {
//        val intent = Intent(this, login::class.java)
//
//        // Clear history so the user can't go back
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//        startActivity(intent)
//
//        // Close the current screen.
//        finish()
//    }
}