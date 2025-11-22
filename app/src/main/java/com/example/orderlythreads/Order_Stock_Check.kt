package com.example.orderlythreads

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Order_Stock_Check : AppCompatActivity() {

    // Lists for the two tabs
    private val pendingList = mutableListOf<OrderItem>()
    private val approvedList = mutableListOf<OrderItem>()

    private lateinit var pendingAdapter: OrdersAdapter
    private lateinit var approvedAdapter: OrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_stock_check)

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tabs
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tabs = listOf(tab1, tab2)

        val recycler = findViewById<RecyclerView>(R.id.ordersRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true

                // Switch adapter based on tab
                when (tab.id) {
                    R.id.tab1 -> recycler.adapter = pendingAdapter
                    R.id.tab2 -> recycler.adapter = approvedAdapter
                }
            }
        }

        // Add initial pending orders
        pendingList.addAll(
            listOf(
                OrderItem("Jeff Marquez", "November 22 - 9 AM", "Custom Order", "Pending Approval", R.drawable.img_merino),
                OrderItem("Amira Gimeno", "November 23 - 2 PM", "Custom Order", "Pending Approval", R.drawable.img_merino)
            )
        )

        // ONLY FOR TESTING
        approvedList.add(
            OrderItem("Sophia Abuyo", "November 24 - 1 PM", "Custom Order", "Approved", R.drawable.img_merino)
        )

        // Initialize adapters
        pendingAdapter = OrdersAdapter(pendingList) { item, pos ->
            approveOrder(item, pos)
        }

        approvedAdapter = OrdersAdapter(approvedList) { _, _ ->
            // Optional: handle clicks in approved tab
        }

        // Default tab (pending)
        recycler.adapter = pendingAdapter
        tab1.isSelected = true
    }

    // Move order from pending to approved
    private fun approveOrder(item: OrderItem, position: Int) {
        // Remove from pending
        pendingList.removeAt(position)
        pendingAdapter.notifyItemRemoved(position)

        // Change status and add to approved list
        val approvedItem = item.copy(status = "Approved")
        approvedList.add(0, approvedItem)
        approvedAdapter.notifyItemInserted(0)
    }
}
