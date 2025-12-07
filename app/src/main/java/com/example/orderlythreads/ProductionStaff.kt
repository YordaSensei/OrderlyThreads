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
import android.widget.ImageButton
import android.widget.Toast

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


            if (orderIndex != -1 && originalStatus != null && finalStatus != null) {
                var itemToMove: ProductionItems? = null

                when (originalStatus) {
                    "Pending" -> {
                        if (orderIndex < pendingList.size) {
                            itemToMove = pendingList.removeAt(orderIndex)
                            pendingAdapter.notifyItemRemoved(orderIndex)
                        }
                    }
                    "Cutting" -> {
                        if (orderIndex < cuttingList.size) {
                            itemToMove = cuttingList.removeAt(orderIndex)
                            cuttingAdapter.notifyItemRemoved(orderIndex)
                        }
                    }
                    "Sewing" -> {
                        if (orderIndex < sewingList.size) {
                            itemToMove = sewingList.removeAt(orderIndex)
                            sewingAdapter.notifyItemRemoved(orderIndex)
                        }
                    }
                    "Finishing" -> {
                        if (finalStatus == "Completed" && orderIndex < finishingList.size) {
                            val itemToUpdate = finishingList[orderIndex]
                            val updatedItem = itemToUpdate.copy(status = "Completed")
                            finishingList[orderIndex] = updatedItem
                            finishingAdapter.notifyItemChanged(orderIndex)
                        }
                        else if (orderIndex < finishingList.size) {
                            itemToMove = finishingList.removeAt(orderIndex)
                            finishingAdapter.notifyItemRemoved(orderIndex)
                        }
                    }
                }

                if (itemToMove != null) {
                    when (finalStatus) {
                        "Pending" -> {
                            pendingList.add(0, itemToMove.copy(status = "Pending"))
                            pendingAdapter.notifyItemInserted(0)
                        }
                        "Cutting" -> {
                            cuttingList.add(0, itemToMove.copy(status = "Cutting"))
                            cuttingAdapter.notifyItemInserted(0)
                        }
                        "Sewing" -> {
                            sewingList.add(0, itemToMove.copy(status = "Sewing"))
                            sewingAdapter.notifyItemInserted(0)
                        }
                        "Finishing" -> {
                            finishingList.add(0, itemToMove.copy(status = "Finishing"))
                            finishingAdapter.notifyItemInserted(0)
                        }
                        "Completed" -> {
                            finishingList.add(0, itemToMove.copy(status = "Completed"))
                            finishingAdapter.notifyItemInserted(0)
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

        recyclerview.adapter = pendingAdapter
        tab1.isSelected = true

        val logoutBtn = findViewById<ImageButton>(R.id.logOutBtn)

        logoutBtn.setOnClickListener {
            val intent = Intent(this, login::class.java)

            // Clear history so the user can't go back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)

            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()

            // Close the current screen.
            finish()
        }

    }
}
