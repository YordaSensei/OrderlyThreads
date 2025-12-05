package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
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

        val spinnerRejectReason = dialogView.findViewById<Spinner>(R.id.spinnerRejectReason)
        val spinnerRejectMaterial = dialogView.findViewById<Spinner>(R.id.spinnerRejectMaterial)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val rejectConfirmButton = dialogView.findViewById<Button>(R.id.rejectConfirmButton)

        val rejectionReasons = listOf("Out of Stock", "Damaged Item", "Wrong Order", "Customer Cancelled", "Other")
        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rejectionReasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRejectReason.adapter = reasonAdapter

        var selectedRejectReason: String? = null
        spinnerRejectReason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRejectReason = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRejectReason = null
            }
        }

        //PLACEHOLDER VALUES
        val lowStockMaterials = listOf("White Thread", "Blue Fabric", "Small Buttons", "Zipper Type A")
        val materialAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lowStockMaterials)
        materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRejectMaterial.adapter = materialAdapter

        var selectedRejectMaterial: String? = null
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
            Toast.makeText(this, "Rejected because: $selectedRejectReason, Low-stock item: $selectedRejectMaterial", Toast.LENGTH_LONG).show()

            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
