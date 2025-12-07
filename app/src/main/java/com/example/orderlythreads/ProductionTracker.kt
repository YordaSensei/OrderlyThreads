package com.example.orderlythreads

import android.app.Dialog
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.content.Intent


class ProductionTracker : AppCompatActivity() {
    private var selectedStatus: Int = 0 // Stores status as a global variable

    private fun sendResultandFinish (finalStatus: String) {
        val receiveOrderIndex = intent.getIntExtra("orderIndex",  -1)
        val originalStatus = intent.getStringExtra("originalStatus")

        //sends data back to be removed
        val resultIntent = Intent().apply {
            putExtra("orderIndex", receiveOrderIndex)
            putExtra("originalStatus", originalStatus)
            putExtra("finalStatus", finalStatus)
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_tracker)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        val clientNameView = findViewById<TextView>(R.id.clientName)
        val scheduleView = findViewById<TextView>(R.id.schedule)
        val labelView = findViewById<TextView>(R.id.label)
        val imageView = findViewById<ImageView>(R.id.garmentContainer)

        val clientName = intent.getStringExtra("clientName")
        val schedule = intent.getStringExtra("orderDate")
        val label = intent.getStringExtra("label")
        val image = intent.getIntExtra("orderImage", 0)

        clientNameView.text = clientName
        scheduleView.text = schedule
        labelView.text = label

        if (image != 0) {
            imageView.setImageResource(image)
        }

        //Spinner adapter
        val spinner = findViewById<Spinner>(R.id.spinnerProcess)
        val items = resources.getStringArray(R.array.process_items)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_process_item,
            items
        )

        adapter.setDropDownViewResource(R.layout.spinner_process_item)
        spinner.adapter = adapter

        val originalStatus = intent.getStringExtra("originalStatus")
        val startingPosition = when (originalStatus) {
            "Cutting" -> 1
            "Sewing" -> 2
            "Finishing" -> 3
            else -> 0
        }

        spinner.setSelection(startingPosition)

        //Icon and text color handler for production status
        val processIcon = listOf<ImageView>(
            findViewById<ImageView>(R.id.hourglassIcon),
            findViewById<ImageView>(R.id.cuttingIcon),
            findViewById<ImageView>(R.id.sewingIcon),
            findViewById<ImageView>(R.id.checkIcon)
        )

        val processTitle =  listOf<TextView>(
            findViewById<TextView>(R.id.hourglassTitle),
            findViewById<TextView>(R.id.cuttingTitle),
            findViewById<TextView>(R.id.sewingTitle),
            findViewById<TextView>(R.id.finishingTitle)
        )

        spinner.onItemSelectedListener =  object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                for  (i in processIcon.indices) {
                    // resets the color to gray for all icons and texts
                    processIcon[i].setColorFilter(Color.parseColor("#8f8f8f"))
                    processTitle[i].setTextColor(Color.parseColor("#8f8f8f"))
                }

                // sets the color to black when an item is selected in the spinner
                processIcon[position].setColorFilter(Color.parseColor("#FF000000"))
                processTitle[position].setTextColor(Color.parseColor("#FF000000"))

                // saves current position to be accessed outside
                selectedStatus = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

            // Pop up alert when order is completed
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_yes_no)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


            val updateBtn =  findViewById<Button>(R.id.updateBtn)

        updateBtn.setOnClickListener {
            val newStatus = items[selectedStatus]
            val originalStatus = intent.getStringExtra("originalStatus")

            if (newStatus == originalStatus)  {
                Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show()
                finish()
            }

            if (selectedStatus == 3) {
                    val dialog  =  Dialog(this)
                    dialog.setContentView(R.layout.popup_yes_no)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    val yesBtn  =  dialog.findViewById<Button>(R.id.yesBtn)
                    val noBtn  =  dialog.findViewById<Button>(R.id.noBtn)

                    yesBtn.setOnClickListener {
                        dialog.dismiss()
                        Toast.makeText(this, "Order Completed!", Toast.LENGTH_SHORT).show()
                        sendResultandFinish("Completed")
                        }

                    noBtn.setOnClickListener {
                        dialog.dismiss()
                    }

                dialog.show()
                }  else {
                sendResultandFinish(newStatus)
                    }
            }
    }
}
