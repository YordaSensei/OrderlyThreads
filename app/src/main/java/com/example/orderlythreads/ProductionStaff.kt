package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.ProductionStatus
import com.example.orderlythreads.Database.ProductionViewModel
import com.example.orderlythreads.Database.ProductionWithDetails

class ProductionStaff : AppCompatActivity() {

    // 1. Lists for the UI
    private val pendingList = mutableListOf<ProductionItems>()
    private val cuttingList = mutableListOf<ProductionItems>()
    private val sewingList = mutableListOf<ProductionItems>()
    private val finishingList = mutableListOf<ProductionItems>()

    // 2. Adapters
    private lateinit var pendingAdapter: ProductionAdapter
    private lateinit var cuttingAdapter: ProductionAdapter
    private lateinit var sewingAdapter: ProductionAdapter
    private lateinit var finishingAdapter: ProductionAdapter

    // 3. ViewModel
    private lateinit var productionViewModel: ProductionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_staff)

        // Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize ViewModel
        productionViewModel = ViewModelProvider(this)[ProductionViewModel::class.java]

        // Setup RecyclerView
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerItems)
        recyclerview.layoutManager = LinearLayoutManager(this)

        // Initialize Adapters
        // We pass the 'openTracker' function to handle clicks
        pendingAdapter = ProductionAdapter(pendingList) { item, _ -> openTracker(item) }
        cuttingAdapter = ProductionAdapter(cuttingList) { item, _ -> openTracker(item) }
        sewingAdapter = ProductionAdapter(sewingList) { item, _ -> openTracker(item) }
        finishingAdapter = ProductionAdapter(finishingList) { item, _ -> openTracker(item) }

        // Set Default Adapter
        recyclerview.adapter = pendingAdapter

        // --- OBSERVE DATABASE ---
        // This automatically updates the UI when DB changes
        productionViewModel.allProductionJobs.observe(this) { dbList ->
            updateUILists(dbList)
        }

        setupTabs(recyclerview)
        setupLogout()
    }

    private fun updateUILists(dbList: List<ProductionWithDetails>) {
        // 1. Clear current lists
        pendingList.clear()
        cuttingList.clear()
        sewingList.clear()
        finishingList.clear()

        // 2. Sort DB data into UI lists
        for (item in dbList) {
            val uiItem = ProductionItems(
                id = item.productionId, // This requires Step 1 to be done
                clientName = item.clientName,
                date = item.dueDate,
                label = "Order #${item.orderId}",
                image = 0,
                status = item.status.name
            )

            when (item.status) {
                ProductionStatus.PENDING -> pendingList.add(uiItem)
                ProductionStatus.CUTTING -> cuttingList.add(uiItem)
                ProductionStatus.SEWING -> sewingList.add(uiItem)
                ProductionStatus.FINISHING -> finishingList.add(uiItem)
                else -> { /* Completed items are ignored */ }
            }
        }

        // 3. Refresh Adapters
        pendingAdapter.notifyDataSetChanged()
        cuttingAdapter.notifyDataSetChanged()
        sewingAdapter.notifyDataSetChanged()
        finishingAdapter.notifyDataSetChanged()
    }

    private fun openTracker(item: ProductionItems) {
        val intent = Intent(this, ProductionTracker::class.java).apply {
            putExtra("productionId", item.id) // Pass the Database ID!
            putExtra("clientName", item.clientName)
            putExtra("orderDate", item.date)
            putExtra("originalStatus", item.status)
        }
        startActivity(intent)
    }

    private fun setupTabs(recyclerview: RecyclerView) {
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)
        val tabs = listOf(tab1, tab2, tab3, tab4)

        tab1.isSelected = true

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
                recyclerview.adapter = when (tab.id) {
                    R.id.tab1 -> pendingAdapter
                    R.id.tab2 -> cuttingAdapter
                    R.id.tab3 -> sewingAdapter
                    else -> finishingAdapter
                }
            }
        }
    }

    private fun setupLogout() {
        findViewById<ImageButton>(R.id.logOutBtn).setOnClickListener {
            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
