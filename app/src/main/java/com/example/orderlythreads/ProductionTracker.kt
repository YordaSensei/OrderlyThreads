package com.example.orderlythreads

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.orderlythreads.Database.ProductionStatus
import com.example.orderlythreads.Database.ProductionViewModel

class ProductionTracker : AppCompatActivity() {

    // 1. Add ViewModel and ID variables
    private lateinit var productionViewModel: ProductionViewModel
    private var productionId: Int = -1
    private var selectedStatusIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_tracker)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 2. Initialize ViewModel
        productionViewModel = ViewModelProvider(this)[ProductionViewModel::class.java]

        // 3. Get Data from Intent (Make sure to get productionId)
        productionId = intent.getIntExtra("productionId", -1)
        val clientName = intent.getStringExtra("clientName")
        val schedule = intent.getStringExtra("orderDate") // Note: Make sure ProductionStaff passes this
        val label = intent.getStringExtra("label") // Note: Make sure ProductionStaff passes this

        // Note: ProductionStaff passes 'image' as empty string in previous code,
        // so this might default to 0 if not handled
        val image = intent.getIntExtra("orderImage", 0)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val clientNameView = findViewById<TextView>(R.id.clientName)
        val scheduleView = findViewById<TextView>(R.id.schedule)
        val labelView = findViewById<TextView>(R.id.label)
        val imageView = findViewById<ImageView>(R.id.garmentContainer)

        scheduleView.text = if (schedule.isNullOrEmpty()) "No Date" else schedule

        clientNameView.text = clientName
        scheduleView.text = schedule
        labelView.text = label

        if (image != 0) {
            imageView.setImageResource(image)
        }

        // --- Your Existing Spinner Logic ---
        val spinner = findViewById<Spinner>(R.id.spinnerProcess)
        val items = resources.getStringArray(R.array.process_items)

        val adapter = ArrayAdapter(this, R.layout.spinner_process_item, items)
        adapter.setDropDownViewResource(R.layout.spinner_process_item)
        spinner.adapter = adapter

        val originalStatusString = intent.getStringExtra("originalStatus")
        val startingPosition = when (originalStatusString) {
            "CUTTING" -> 1
            "SEWING" -> 2
            "FINISHING" -> 3
            else -> 0 // PENDING
        }

        spinner.setSelection(startingPosition)

        val processIcon = listOf<ImageView>(
            findViewById(R.id.hourglassIcon),
            findViewById(R.id.cuttingIcon),
            findViewById(R.id.sewingIcon),
            findViewById(R.id.checkIcon)
        )

        val processTitle = listOf<TextView>(
            findViewById(R.id.hourglassTitle),
            findViewById(R.id.cuttingTitle),
            findViewById(R.id.sewingTitle),
            findViewById(R.id.finishingTitle)
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                for (i in processIcon.indices) {
                    processIcon[i].setColorFilter(Color.parseColor("#8f8f8f"))
                    processTitle[i].setTextColor(Color.parseColor("#8f8f8f"))
                }
                processIcon[position].setColorFilter(Color.parseColor("#FF000000"))
                processTitle[position].setTextColor(Color.parseColor("#FF000000"))

                selectedStatusIndex = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val updateBtn = findViewById<Button>(R.id.updateBtn)

        updateBtn.setOnClickListener {
            // Check if ID is valid
            if (productionId == -1) {
                Toast.makeText(this, "Error: Invalid ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Logic for "Finishing" -> "Completed" Dialog
            if (selectedStatusIndex == 3) {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_yes_no)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                val yesBtn = dialog.findViewById<Button>(R.id.yesBtn)
                val noBtn = dialog.findViewById<Button>(R.id.noBtn)

                yesBtn.setOnClickListener {
                    dialog.dismiss()
                    // UPDATE DB TO COMPLETED
                    saveToDatabase(ProductionStatus.FINISHING)
                }

                noBtn.setOnClickListener {
                    dialog.dismiss()
                    // Just update to Finishing? Or do nothing?
                    // Assuming we keep it at Finishing if they click no
                    saveToDatabase(ProductionStatus.FINISHING)
                }

                dialog.show()
            } else {
                // Map Spinner Index to Enum
                val newStatusEnum = when (selectedStatusIndex) {
                    0 -> ProductionStatus.PENDING
                    1 -> ProductionStatus.CUTTING
                    2 -> ProductionStatus.SEWING
                    3 -> ProductionStatus.FINISHING
                    else -> ProductionStatus.PENDING
                }

                saveToDatabase(newStatusEnum)
            }
        }
    }

    // Helper function to call the ViewModel
    private fun saveToDatabase(status: ProductionStatus) {
        productionViewModel.updateStatusById(productionId, status)
        Toast.makeText(this, "Status Updated!", Toast.LENGTH_SHORT).show()
        finish() // Return to previous screen
    }
}
