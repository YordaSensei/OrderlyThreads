package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Approve_Order_Check : AppCompatActivity() {

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

        val index = intent.getIntExtra("orderIndex", -1)
        orderName.text = intent.getStringExtra("orderName")
        orderDateTime.text = intent.getStringExtra("orderDate")
        orderLabel.text = intent.getStringExtra("orderLabel")
        orderShirtImage.setImageResource(intent.getIntExtra("orderImage", 0))

        // Approve order
        verifyButton.setOnClickListener {
            // Send result back to Order_Stock_Check
            val resultIntent = Intent()
            resultIntent.putExtra("orderIndex", index)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Reject order
        rejectButton.setOnClickListener {
            showRejectDialog()
        }
    }

    private fun showRejectDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject_order, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val lowStockMaterial = dialogView.findViewById<TextView>(R.id.lowStockMaterial)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val rejectConfirmButton = dialogView.findViewById<Button>(R.id.rejectConfirmButton)

        // TODO: Replace with actual low stock material
        lowStockMaterial.text = "The following material is low in stock: White Thread"

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        rejectConfirmButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
