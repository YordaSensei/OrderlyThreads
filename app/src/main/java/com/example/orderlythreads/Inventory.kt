package com.example.orderlythreads

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.Inventory
import com.example.orderlythreads.Database.InventoryDao
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.InventoryRepository
import android.widget.AdapterView

class Inventory : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var inventoryDao: InventoryDao

    // Category names matching the tabs
    private val categoryNames = listOf("Fabric", "Color/Pattern", "Basic Materials", "Accents")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inventory)
        
        setupWindowInsets()

        // Initialize Room Database components
        inventoryDao = OrderlyThreadsDatabase.getDatabase(applicationContext).inventoryDao()
        val repository = InventoryRepository(inventoryDao)
        val viewModelFactory = InventoryViewModelFactory(repository)
        inventoryViewModel = ViewModelProvider(this, viewModelFactory).get(InventoryViewModel::class.java)

        setupTabs()
        setupRecyclerView()

        // Observe inventory items from ViewModel
        inventoryViewModel.inventoryItems.observe(this) { inventoryList ->
            inventoryList?.let { adapter.updateData(it.toMutableList()) }
        }
        
        // Set initial state for the first tab and trigger data load
        findViewById<TextView>(R.id.tab1).isSelected = true
        inventoryViewModel.setCategory(categoryNames[0])
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupTabs() {
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)
        val addButton = findViewById<FrameLayout>(R.id.addButton)
        
        val tabs = listOf(tab1, tab2, tab3, tab4)

        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
                // Update selected category in ViewModel
                inventoryViewModel.setCategory(categoryNames[index])
            }
        }
        
        addButton.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun setupRecyclerView() {
        val recycler = findViewById<RecyclerView>(R.id.inventoryRecycler)
        adapter = InventoryAdapter(mutableListOf()) { item, position, anchorView ->
            showItemMenu(item, position, anchorView)
        }
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this, 2)
    }

    private fun showItemMenu(item: Inventory, position: Int, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.inventory_card_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    showEditDialog(item) { updatedItem ->
                        inventoryViewModel.updateItem(updatedItem)
                        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.add -> {
                    val newQuantity = item.quantity + 1
                    val updatedItem = item.copy(quantity = newQuantity)
                    inventoryViewModel.updateItem(updatedItem)
                    Toast.makeText(this, "${item.material} quantity: $newQuantity", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.delete -> {
                    showDeleteConfirmationDialog(item)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // region Dialogs
    private fun showAddItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.title) // Corrected ID
        val itemNameEditText = dialogView.findViewById<EditText>(R.id.editItemName)
        val itemQuantityEditText = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        val btnAddItem = dialogView.findViewById<Button>(R.id.btn_add_item)
        val btnCancelAddItem = dialogView.findViewById<Button>(R.id.btn_cancel_add_item)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Make dialog background transparent
        dialog.setCancelable(true)

        selectImageButton.setOnClickListener {
            Toast.makeText(this, "Image selection is not implemented yet", Toast.LENGTH_SHORT).show()
        }

        btnAddItem.setOnClickListener { _ ->
            val material = itemNameEditText.text.toString()
            val quantityStr = itemQuantityEditText.text.toString()

            if (material.isNotEmpty() && quantityStr.isNotEmpty()) {
                val quantity = quantityStr.toIntOrNull() ?: 0
                val currentCategory = inventoryViewModel.selectedCategory.value ?: "Unknown"
                val newItem = Inventory(category = currentCategory, material = material, quantity = quantity, imageRes = R.drawable.img_placeholder)
                inventoryViewModel.addItem(newItem)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelAddItem.setOnClickListener { _ ->
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDialog(
        item: Inventory,
        onSave: (Inventory) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val quantityInput = dialogView.findViewById<EditText>(R.id.editQuantity)

        nameInput.setText(item.material)
        quantityInput.setText(item.quantity.toString())

        AlertDialog.Builder(this)
            .setTitle("Edit Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newQuantityStr = quantityInput.text.toString().trim()
                if (newName.isNotEmpty() && newQuantityStr.isNotEmpty()) {
                    val newQuantity = newQuantityStr.toIntOrNull() ?: 0
                    val updatedItem = item.copy(material = newName, quantity = newQuantity)
                    onSave(updatedItem)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(item: Inventory) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete \"${item.material}\"?")
            .setPositiveButton("Delete") { _, _ ->
                inventoryViewModel.deleteItem(item)
                Toast.makeText(this, "${item.material} deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    // endregion
}
