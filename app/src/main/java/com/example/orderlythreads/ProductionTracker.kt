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


class ProductionTracker : AppCompatActivity() {
    private var selectedStatus: Int = 0 // Stores status as a global variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_tracker)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

            val yesBtn  =  dialog.findViewById<Button>(R.id.yesBtn)
            val noBtn  =  dialog.findViewById<Button>(R.id.noBtn)
            val completeBtn =  findViewById<Button>(R.id.completeBtn)

            completeBtn.setOnClickListener { view ->
                    if (selectedStatus == 3) {
                    dialog.show()

                    yesBtn.setOnClickListener {
                        Toast.makeText(this, "Order Completed!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

                    noBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                }  else {
                Toast.makeText(this, "Order still not yet finished!", Toast.LENGTH_SHORT).show()
                    }
            }
    }
}
