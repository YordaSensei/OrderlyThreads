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

class Order_Stock_Check : AppCompatActivity() {

    private val pendingList = mutableListOf<OrderItem>()
    private val approvedList = mutableListOf<OrderItem>()

    private lateinit var pendingAdapter: OrdersAdapter
    private lateinit var approvedAdapter: OrdersAdapter

    companion object {
        const val REQUEST_APPROVE_ORDER = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_stock_check)

        // System bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupFooter()

        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tabs = listOf(tab1, tab2)

        val recycler = findViewById<RecyclerView>(R.id.ordersRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
                recycler.adapter = if (tab.id == R.id.tab1) pendingAdapter else approvedAdapter
            }
        }

        // Sample pending orders (Commented out for dynamic data implementation)
//        pendingList.addAll(
//            listOf(
//                OrderItem("Jeff Marquez", "November 22 - 9 AM", "Custom Order", "Pending Approval", R.drawable.img_fabric_merino),
//                OrderItem("Amira Gimeno", "November 23 - 2 PM", "Custom Order", "Pending Approval", R.drawable.img_fabric_merino)
//            )
//        )

        // Sample approved orders (Commented out for dynamic data implementation)
//        approvedList.add(
//            OrderItem("Sophia Abuyo", "November 24 - 1 PM", "Custom Order", "Approved", R.drawable.img_fabric_merino)
//        )

        // Adapters
        pendingAdapter = OrdersAdapter(pendingList) { item, position ->
            val intent = Intent(this, Approve_Order_Check::class.java).apply {
                putExtra("orderIndex", position) // pass index to track
                putExtra("orderName", item.name)
                putExtra("orderDate", item.date)
                putExtra("orderLabel", item.label)
                putExtra("orderImage", item.imageRes)
            }
            startActivityForResult(intent, REQUEST_APPROVE_ORDER)
        }

        approvedAdapter = OrdersAdapter(approvedList) { _, _ -> }

        // Default tab
        recycler.adapter = pendingAdapter
        tab1.isSelected = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_APPROVE_ORDER && resultCode == RESULT_OK && data != null) {
            val index = data.getIntExtra("orderIndex", -1)
            val status = data.getStringExtra("orderStatus") ?: "Approved"
            
            if (index != -1 && index < pendingList.size) {
                val approvedItem = pendingList.removeAt(index)
                val updatedItem = approvedItem.copy(status = status)
                approvedList.add(0, updatedItem)
                pendingAdapter.notifyItemRemoved(index)
                approvedAdapter.notifyItemInserted(0)
            }
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
