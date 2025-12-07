package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.Orders
import com.example.orderlythreads.Database.OrdersRepository
import com.example.orderlythreads.Database.OrdersViewModel
import com.example.orderlythreads.Database.OrdersViewModelFactory

class Order_Stock_Check : AppCompatActivity() {

    private val pendingList = mutableListOf<OrderItem>()
    private val approvedList = mutableListOf<OrderItem>()

    private lateinit var pendingAdapter: OrdersAdapter
    private lateinit var approvedAdapter: OrdersAdapter
    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_stock_check)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        val database = OrderlyThreadsDatabase.getDatabase(this)
        val repository = OrdersRepository(database.ordersDao())
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return OrdersViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        
        ordersViewModel = ViewModelProvider(this, viewModelFactory).get(OrdersViewModel::class.java)

        setupFooter()

        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tabs = listOf(tab1, tab2)

        val recycler = findViewById<RecyclerView>(R.id.ordersRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        // Initialize Adapters
        pendingAdapter = OrdersAdapter(pendingList) { item, position ->
            val intent = Intent(this, Approve_Order_Check::class.java).apply {
                putExtra("orderId", item.id) // Pass ID to update DB
                putExtra("orderName", item.name)
                putExtra("orderDate", item.date)
                putExtra("orderLabel", item.label)
                putExtra("orderImage", item.imageRes)
            }
            startActivity(intent)
        }

        approvedAdapter = OrdersAdapter(approvedList) { _, _ -> }

        // Default tab
        recycler.adapter = pendingAdapter
        tab1.isSelected = true

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
                recycler.adapter = if (tab.id == R.id.tab1) pendingAdapter else approvedAdapter
            }
        }

        // Observe Orders from Database
        ordersViewModel.allOrders.observe(this) { orders ->
            pendingList.clear()
            approvedList.clear()

            orders.forEach { order ->
                // Skip Rejected orders
                if (order.status == "Rejected") return@forEach

                // Map Entity to UI Model
                val item = OrderItem(
                    id = order.orderId,
                    name = order.clientName,
                    date = order.orderDate,
                    label = "Custom Order", // Default label or derive if needed
                    status = order.status,
                    imageRes = if (order.upperDesignId != 0) order.upperDesignId else R.drawable.img_placeholder // Handle 0/null
                )

                if (item.status == "Approved") {
                    approvedList.add(item)
                } else {
                    pendingList.add(item) // "Pending Approval"
                }
            }

            pendingAdapter.updateData(pendingList)
            approvedAdapter.updateData(approvedList)
        }
    }

    private fun setupFooter() {
        val inventoryBtn = findViewById<android.view.View>(R.id.inventoryBtn)
        inventoryBtn.setOnClickListener {
            val intent = Intent(this, Inventory::class.java)
            startActivity(intent)
        }
        
        val logOutBtn = findViewById<android.view.View>(R.id.logOutBtn)
        logOutBtn.setOnClickListener {
            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
