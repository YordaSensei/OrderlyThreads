package com.example.orderlythreads

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.InventoryRepository
import com.example.orderlythreads.Database.InventoryViewModel
import com.example.orderlythreads.Database.InventoryViewModelFactory
import com.example.orderlythreads.Database.OrdersRepository
import com.example.orderlythreads.Database.OrdersViewModel
import com.example.orderlythreads.Database.Orders
import com.example.orderlythreads.Database.Inventory
import com.example.orderlythreads.Database.OrderCheck
import com.example.orderlythreads.Database.OrderCheckDao
import com.example.orderlythreads.Database.OrderCheckRepository
import com.example.orderlythreads.Database.OrderCheckViewModel

data class MaterialItem(val category: String, val name: String, val stock: Int)

class Approve_Order_Check : AppCompatActivity() {

    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var ordersViewModel: OrdersViewModel
    private lateinit var orderCheckViewModel: OrderCheckViewModel
    private var orderMaterials: List<MaterialItem> = emptyList()
    private var matchedInventoryItems: List<Inventory> = emptyList() 
    private var allInventoryItems: List<Inventory> = emptyList()
    private var currentOrderId: Int = -1
    private var currentOrder: Orders? = null

    // UI Variables
    private lateinit var tvOrderId: TextView
    private lateinit var tvGarment: TextView
    private lateinit var tvFabric: TextView
    private lateinit var tvColorPattern: TextView
    private lateinit var tvNotes: TextView

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
        val materialsListContainer = findViewById<LinearLayout>(R.id.materialsList)

        // Initialize Detail Views
        tvOrderId = findViewById(R.id.orderIdVar)
        tvGarment = findViewById(R.id.garmentVar)
        tvFabric = findViewById(R.id.fabricVar)
        tvColorPattern = findViewById(R.id.cpVar)
        tvNotes = findViewById(R.id.notesVar)

        // Get data from Intent
        currentOrderId = intent.getIntExtra("orderId", -1)
        orderName.text = intent.getStringExtra("orderName")
        orderDateTime.text = intent.getStringExtra("orderDate")
        orderLabel.text = intent.getStringExtra("orderLabel")
        orderShirtImage.setImageResource(intent.getIntExtra("orderImage", 0))

        // Setup ViewModels
        val database = OrderlyThreadsDatabase.getDatabase(applicationContext)
        
        // Inventory ViewModel
        val inventoryRepo = InventoryRepository(database.inventoryDao())
        val inventoryFactory = InventoryViewModelFactory(inventoryRepo)
        inventoryViewModel = ViewModelProvider(this, inventoryFactory).get(InventoryViewModel::class.java)

        // Orders ViewModel
        val ordersRepo = OrdersRepository(database.ordersDao())
        val ordersFactory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return OrdersViewModel(ordersRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        ordersViewModel = ViewModelProvider(this, ordersFactory).get(OrdersViewModel::class.java)

        // OrderCheck ViewModel
        val orderCheckRepo = OrderCheckRepository(database.orderCheckDao())
        val orderCheckFactory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(OrderCheckViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return OrderCheckViewModel(orderCheckRepo) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        orderCheckViewModel = ViewModelProvider(this, orderCheckFactory).get(OrderCheckViewModel::class.java)


        // 1. Fetch the specific order
        if (currentOrderId != -1) {
            ordersViewModel.fetchOrder(currentOrderId)
        }

        // 2. Observe the fetched order
        ordersViewModel.currentOrder.observe(this) { order ->
            currentOrder = order
            if (order != null) {
                populateOrderDetails(order)
                if (allInventoryItems.isNotEmpty()) {
                    filterMaterialsForOrder(order, allInventoryItems, materialsListContainer)
                }
            }
        }

        // 3. Observe inventory items
        inventoryViewModel.inventoryItems.observe(this) { inventoryList ->
            allInventoryItems = inventoryList
            if (currentOrder != null) {
                filterMaterialsForOrder(currentOrder!!, inventoryList, materialsListContainer)
                populateOrderDetails(currentOrder!!) 
            } else if (currentOrderId == -1) {
                orderMaterials = inventoryList
                    .filter { it.category != "Basic Materials" }
                    .map { MaterialItem(it.category, it.material, it.quantity) }
                displayOrderMaterials(materialsListContainer)
            }
        }

        // Approve order
        verifyButton.setOnClickListener {
            val lowStockItems = orderMaterials.filter { it.stock <= 15 }.map { it.name }
            if (lowStockItems.isNotEmpty()) {
                Toast.makeText(this, "Warning: Approving order with low stock items: $lowStockItems", Toast.LENGTH_LONG).show()
            }
            
            if (currentOrderId != -1) {
                // 1. Update Order Status
                ordersViewModel.updateOrderStatus(currentOrderId, "Approved")

                // 2. Deduct Materials & Create Order Check Records
                matchedInventoryItems.forEach { inventoryItem ->
                    // Deduct stock
                    val newQuantity = if (inventoryItem.quantity >= 2) inventoryItem.quantity - 2 else 0
                    val updatedItem = inventoryItem.copy(quantity = newQuantity)
                    inventoryViewModel.updateItem(updatedItem)

                    // Create Order Check Record (Approved) - ONLY for Approved
                    val orderCheck = OrderCheck(
                        orderId = currentOrderId,
                        inventoryId = inventoryItem.id,
                        status = "Approved"
                    )
                    orderCheckViewModel.addOrderCheck(orderCheck)
                }

                Toast.makeText(this, "Order Approved & Stock Deducted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: Order ID not found", Toast.LENGTH_SHORT).show()
            }
            finish()
        }

        // Reject order
        rejectButton.setOnClickListener {
            val lowStockItems = orderMaterials.filter { it.stock <= 15 }.map { it.name }
            showRejectDialog(lowStockItems)
        }
    }

    private fun populateOrderDetails(order: Orders) {
        tvOrderId.text = "OD${order.orderId}"

        val garmentResId = if (order.upperDesignId != 0) order.upperDesignId else order.lowerDesignId
        var garmentName = "(Not specified)"
        if (garmentResId != 0) {
            try {
                val resEntryName = resources.getResourceEntryName(garmentResId)
                garmentName = resEntryName
                    .replace("casual_upper_", "")
                    .replace("formal_upper_", "")
                    .replace("casual_lower_", "")
                    .replace("formal_lower_", "")
                    .replace("_", " ")
                    .split(" ").joinToString(" ") { it.capitalize() }
            } catch (e: Exception) {
                garmentName = "Custom Garment"
            }
        }
        tvGarment.text = garmentName

        val fabricId = if (order.upperFabricId != 0) order.upperFabricId else order.lowerFabricId
        var fabricName = "(Not specified)"
        if (fabricId != 0) {
            val matchedFabric = allInventoryItems.find { it.id == fabricId }
            if (matchedFabric != null) {
                fabricName = matchedFabric.material
            }
        }
        tvFabric.text = fabricName

        val colorHex = if (!order.upperColorHex.isNullOrEmpty() && order.upperColorHex != "0") order.upperColorHex else order.lowerColorHex
        var colorName = colorHex
        if (colorName.isNullOrEmpty() || colorName == "0") {
             colorName = "(Not specified)"
        } else {
             if (colorName.startsWith("-")) colorName = "Custom Color"
        }
        tvColorPattern.text = colorName

        tvNotes.text = if (order.additionalNotes.isNotBlank()) order.additionalNotes else "(Not specified)"
    }

    private fun filterMaterialsForOrder(order: Orders, inventoryList: List<Inventory>, container: LinearLayout) {
        val requiredIds = mutableSetOf<Int>()
        if (order.upperFabricId != 0) requiredIds.add(order.upperFabricId)
        if (order.lowerFabricId != 0) requiredIds.add(order.lowerFabricId)
        if (order.upperAccentDesignId != 0) requiredIds.add(order.upperAccentDesignId)
        if (order.lowerAccentDesignId != 0) requiredIds.add(order.lowerAccentDesignId)

        matchedInventoryItems = inventoryList.filter { inventoryItem ->
            requiredIds.contains(inventoryItem.id)
        }

        orderMaterials = matchedInventoryItems.map { MaterialItem(it.category, it.material, it.quantity) }
        displayOrderMaterials(container)
    }

    private fun displayOrderMaterials(materialsListContainer: LinearLayout) {
        materialsListContainer.removeAllViews()

        if (orderMaterials.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "No specific materials identified for this order."
            emptyView.setPadding(16, 16, 16, 16)
            materialsListContainer.addView(emptyView)
            return
        }

        for (material in orderMaterials) {
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_material_row, materialsListContainer, false)
            
            val categoryText = itemView.findViewById<TextView>(R.id.materialCategory)
            val nameText = itemView.findViewById<TextView>(R.id.materialName)
            val stockText = itemView.findViewById<TextView>(R.id.materialStock)

            categoryText.text = material.category
            nameText.text = material.name
            stockText.text = material.stock.toString()

            if (material.stock <= 15) {
                stockText.setTextColor(Color.RED)
            } else {
                stockText.setTextColor(Color.parseColor("#4CAF50")) // Green
            }
            
            materialsListContainer.addView(itemView)
        }
    }

    private fun showRejectDialog(lowStockMaterials: List<String>) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject_order, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)

        val dialog = dialogBuilder.create()

        val spinnerRejectReason = dialogView.findViewById<Spinner>(R.id.spinnerRejectReason)
        val spinnerRejectMaterial = dialogView.findViewById<Spinner>(R.id.spinnerRejectMaterial)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        val rejectConfirmButton = dialogView.findViewById<Button>(R.id.rejectConfirmButton)

        val categories = orderMaterials
            .map { it.category }
            .distinct()
            
        val rejectionReasons = if (categories.isNotEmpty()) categories else listOf("None")
        val reasonAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rejectionReasons)
        reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRejectReason.adapter = reasonAdapter

        var selectedRejectReason: String? = null
        var selectedRejectMaterial: String? = null

        spinnerRejectReason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRejectReason = parent?.getItemAtPosition(position).toString()

                val filteredItems = orderMaterials.filter { 
                    it.category == selectedRejectReason
                }.map { it.name }

                val materialSource = if (filteredItems.isNotEmpty()) filteredItems else listOf("None")
                
                val materialAdapter = ArrayAdapter(this@Approve_Order_Check, android.R.layout.simple_spinner_item, materialSource)
                materialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerRejectMaterial.adapter = materialAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRejectReason = null
            }
        }

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
            if (currentOrderId != -1) {
                ordersViewModel.updateOrderStatus(currentOrderId, "Rejected")
                // Removed OrderCheck creation for rejected orders as requested
                Toast.makeText(this@Approve_Order_Check, "Order Rejected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@Approve_Order_Check, "Error: Order ID not found", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }
}
