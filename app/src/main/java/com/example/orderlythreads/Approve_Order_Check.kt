package com.example.orderlythreads

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

data class MaterialItem(val category: String, val name: String, val stock: Int)

class Approve_Order_Check : AppCompatActivity() {

    // Placeholder data representing database items
//    private val placeholderMaterials = listOf(
//        MaterialItem("Fabric", "Cotton", 20),
//        MaterialItem("Basic Materials", "White Thread", 5),
//        MaterialItem("Accents", "Small Buttons", 100),
//        MaterialItem("Accents", "Zipper Type A", 12)
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.approve_order_check)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val back = findViewById<Button>(R.id.back_button)
        back.setOnClickListener { finish() }

        val orderName = findViewById<TextView>(R.id.orderName)
        val orderDateTime = findViewById<TextView>(R.id.orderDateTime)
        val orderLabel = findViewById<TextView>(R.id.orderLabel)
        val orderShirtImage = findViewById<ImageView>(R.id.orderShirtImage)
        val verifyButton = findViewById<TextView>(R.id.verifyStockButton)
        val rejectButton = findViewById<TextView>(R.id.rejectButton)
        val materialsListContainer = findViewById<LinearLayout>(R.id.materialsList)

        val index = intent.getIntExtra("orderIndex", -1)
        orderName.text = intent.getStringExtra("orderName")
        orderDateTime.text = intent.getStringExtra("orderDate")
        orderLabel.text = intent.getStringExtra("orderLabel")
        orderShirtImage.setImageResource(intent.getIntExtra("orderImage", 0))

        // Populate materials and check stock
        materialsListContainer.removeAllViews()
        val lowStockItems = mutableListOf<String>()

        for (material in placeholderMaterials) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_material_row, materialsListContainer, false)
            
            val categoryText = itemView.findViewById<TextView>(R.id.materialCategory)
            val nameText = itemView.findViewById<TextView>(R.id.materialName)
            val stockText = itemView.findViewById<TextView>(R.id.materialStock)

            categoryText.text = material.category
            nameText.text = material.name
            stockText.text = material.stock.toString()

            if (material.stock <= 15) {
                stockText.setTextColor(Color.RED)
                lowStockItems.add(material.name)
            } else {
                stockText.setTextColor(Color.parseColor("#4CAF50")) // Green
            }
            
            materialsListContainer.addView(itemView)
        }

        // Approve order
        verifyButton.setOnClickListener {
            if (lowStockItems.isNotEmpty()) {
                Toast.makeText(this, "Warning: Approving order with low stock items: $lowStockItems", Toast.LENGTH_LONG).show()
            }
            // Send result back to Order_Stock_Check with "Approved" status
            val resultIntent = Intent()
            resultIntent.putExtra("orderIndex", index)
            resultIntent.putExtra("orderStatus", "Approved")
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Reject order
        rejectButton.setOnClickListener {
            showRejectDialog(index, lowStockItems)
        }
    }

    private fun showRejectDialog(index: Int, lowStockMaterials: List<String>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject_order, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val spinnerRejectReason = dialogView.findViewById<Spinner>(R.id.spinnerRejectReason)
        val spinnerRejectMaterial = dialogView.findViewById<Spinner>(R.id.spinnerRejectMaterial)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val rejectConfirmButton = dialogView.findViewById<Button>(R.id.rejectConfirmButton)

        val rejectionReasons = listOf("Fabric", "Color/Pattern", "Basic Materials", "Accents")
        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rejectionReasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRejectReason.adapter = reasonAdapter

        var selectedRejectReason: String? = null
        var selectedRejectMaterial: String? = null

        // Listener for the Category Spinner
        spinnerRejectReason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRejectReason = parent?.getItemAtPosition(position).toString()

                // Filter items based on the selected category and if they are low stock
                val filteredItems = placeholderMaterials.filter { 
                    it.category == selectedRejectReason && it.stock <= 15
                }.map { it.name }

                val materialSource = if (filteredItems.isNotEmpty()) filteredItems else listOf("None")
                
                val materialAdapter = ArrayAdapter(this@Approve_Order_Check, android.R.layout.simple_spinner_item, materialSource)
                materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRejectMaterial.adapter = materialAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRejectReason = null
            }
        }

        // Listener for the Material Spinner
        spinnerRejectMaterial.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRejectMaterial = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRejectMaterial = null
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        rejectConfirmButton.setOnClickListener {
            // Send result back to Order_Stock_Check with "Rejected" status
            val resultIntent = Intent()
            resultIntent.putExtra("orderIndex", index)
            resultIntent.putExtra("orderStatus", "Rejected")

            setResult(RESULT_OK, resultIntent) 
            
            Toast.makeText(this@Approve_Order_Check, "Order Rejected", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
