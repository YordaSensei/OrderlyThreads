package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.result.contract.ActivityResultContracts

class ProductionStaff : AppCompatActivity() {

    //global lists of production to manage the individual data sets
    private val pendingList = mutableListOf<ProductionItems>()
    private val cuttingList = mutableListOf<ProductionItems>()
    private val sewingList = mutableListOf<ProductionItems>()
    private val finishingList = mutableListOf<ProductionItems>()

    //separate adapters to manage each list (for tab switching)
    private lateinit var pendingAdapter: ProductionAdapter
    private lateinit var cuttingAdapter: ProductionAdapter
    private lateinit var sewingAdapter: ProductionAdapter
    private lateinit var finishingAdapter: ProductionAdapter

    private val  trackOrderLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK && result.data != null) {
            val data = result.data!!
            val orderIndex = data.getIntExtra("orderIndex", -1)
            val originalStatus = data.getStringExtra("originalStatus")
            val finalStatus = data.getStringExtra("finalStatus")


            if (orderIndex != -1) {
                when (originalStatus) {
                    "Pending" -> {
                        val updatedItem = pendingList.removeAt(orderIndex) // deletes the item from the pending list
                        pendingAdapter.notifyItemRemoved (orderIndex)

                        if (finalStatus == "Cutting") {
                            cuttingList.add(0, updatedItem.copy(status = "Cutting"))
                            cuttingAdapter.notifyItemInserted(0)
                        }
                    }
                    "Cutting" -> {
                        val updatedItem = cuttingList.removeAt(orderIndex) // deletes the item from the pending list
                        cuttingAdapter.notifyItemRemoved(orderIndex)

                        if (finalStatus ==  "Sewing") {
                            sewingList.add(0, updatedItem.copy(status = "Sewing"))
                            sewingAdapter.notifyItemInserted(0)
                        }
                    }
                    "Sewing" -> {
                        val updatedItem =sewingList.removeAt(orderIndex) // deletes the item from the pending list
                        sewingAdapter.notifyItemRemoved(orderIndex)

                        if (finalStatus == "Finishing") {
                            finishingList.add(0, updatedItem.copy(status = "Finishing"))
                            finishingAdapter.notifyItemInserted(0)
                        } else if (finalStatus == "Completed") {
                            finishingList.add(0, updatedItem.copy(status = "Completed"))
                            finishingAdapter.notifyItemInserted(0)
                        }
                    }
                    "Finishing" -> {
                        if (finalStatus == "Completed") {
                            val itemToUpdate = finishingList[orderIndex] //get item
                            val updatedItem = itemToUpdate.copy(status = "Completed") //variable for updating item status
                            finishingList[orderIndex] = updatedItem //updates the item status with the new one  ^
                            finishingAdapter.notifyItemChanged(orderIndex)
                        }
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_staff)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerItems)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)

        val tabs = listOf(tab1, tab2, tab3, tab4)

        //loop that handles selection of each tab
        tabs.forEach { tab ->
            tab.setOnClickListener {

                tabs.forEach { it.isSelected = false } //deselects the current tab open

                tab.isSelected = true //highlights the selected tab
                recyclerview.adapter = if (tab.id == R.id.tab1) pendingAdapter
                else if (tab.id == R.id.tab2) cuttingAdapter
                else if (tab.id == R.id.tab3) sewingAdapter
                else finishingAdapter
            }
        }

        pendingAdapter = ProductionAdapter(pendingList) { item, position ->
            val intent = Intent(this, ProductionTracker::class.java).apply {
                putExtra("orderIndex", position)
                putExtra("clientName", item.clientName)
                putExtra("orderDate", item.date)
                putExtra("orderLabel", item.label)
                putExtra("orderImage", item.image)
                putExtra("originalStatus", item.status)
            }
            trackOrderLauncher.launch(intent)
        }

        cuttingAdapter = ProductionAdapter(cuttingList) { item, position ->
            val intent = Intent(this, ProductionTracker::class.java).apply {
                putExtra("orderIndex", position)
                putExtra("clientName", item.clientName)
                putExtra("orderDate", item.date)
                putExtra("orderLabel", item.label)
                putExtra("orderImage", item.image)
                putExtra("originalStatus", item.status)
            }
            trackOrderLauncher.launch(intent)
        }

        sewingAdapter = ProductionAdapter(sewingList) { item, position ->
            val intent = Intent(this, ProductionTracker::class.java).apply {
                putExtra("orderIndex", position)
                putExtra("clientName", item.clientName)
                putExtra("orderDate", item.date)
                putExtra("orderLabel", item.label)
                putExtra("orderImage", item.image)
                putExtra("originalStatus", item.status)
            }
            trackOrderLauncher.launch(intent)
        }

        finishingAdapter = ProductionAdapter(finishingList) {  _, _ ->}

        pendingList.addAll(
            listOf(
                ProductionItems(R.drawable.gray_background, "Amira Gimeno", "December 23 - 4PM", "Custom Order", "Pending"),
                ProductionItems(R.drawable.gray_background, "Nathan Alilam", "December 26 - 4PM", "Custom Order", "Pending")
            )
        )

        cuttingList.addAll(
            listOf(
                ProductionItems(R.drawable.yellow_background, "Lorraine Sevilla", "December 23 - 4PM", "Custom Order", "Cutting"),
                ProductionItems(R.drawable.yellow_background, "Josh Malto", "December 26 - 4PM", "Custom Order", "Cutting")
            )
        )

        sewingList.addAll(
            listOf(
                ProductionItems(R.drawable.orange_background, "Client D", "December 22 - 1PM", "Prototype", "Sewing")
            )
        )

        finishingList.addAll(
            listOf(
                ProductionItems(R.drawable.green_background, "Client F", "December 20 - 9AM", "Final Uniforms", "Completed")
            )
        )

        pendingAdapter.notifyDataSetChanged()
        cuttingAdapter.notifyDataSetChanged()
        sewingAdapter.notifyDataSetChanged()
        finishingAdapter.notifyDataSetChanged()

        recyclerview.adapter = pendingAdapter
        tab1.isSelected = true

    }
}
