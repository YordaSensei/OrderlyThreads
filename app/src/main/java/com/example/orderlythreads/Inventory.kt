package com.example.orderlythreads

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Inventory : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter
    private var selectedTab = 0

    // region Data Lists
    private val fabricData = mutableListOf(
        InventoryItem("Merino", "20", R.drawable.img_merino),
        InventoryItem("Souffle Yarn", "20", R.drawable.img_scouffle_yarn),
        InventoryItem("Lambswool", "20", R.drawable.img_lambswool),
        InventoryItem("Cashmere", "20", R.drawable.img_cashmere),
        InventoryItem("Milano Ribbed", "20", R.drawable.img_milano_ribbed),
        InventoryItem("Alpaca", "20", R.drawable.img_alpaca)
    )

    private val colorData = mutableListOf(
        InventoryItem("Solid", "10", R.drawable.img_solid_color),
        InventoryItem("Striped", "10", R.drawable.img_striped_color),
        InventoryItem("Checkered", "10", R.drawable.img_checkered)
    )

    private val closuresData = mutableListOf(
        InventoryItem("Button", "20", R.drawable.img_button),
        InventoryItem("Zipper", "20", R.drawable.img_zipper)
    )

    private val accentsData = mutableListOf(
        InventoryItem("Bows", "20", R.drawable.img_bow),
        InventoryItem("Rivets", "20", R.drawable.img_pearls)
    )
    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inventory)
        
        setupWindowInsets()
        setupTabs()
        setupRecyclerView()
        
        // Set initial state
        findViewById<TextView>(R.id.tab1).isSelected = true
        updateDataForSelectedTab()
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
                selectedTab = index
                updateDataForSelectedTab()
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

    private fun updateDataForSelectedTab() {
        when (selectedTab) {
            0 -> adapter.updateData(fabricData)
            1 -> adapter.updateData(colorData)
            2 -> adapter.updateData(closuresData)
            3 -> adapter.updateData(accentsData)
        }
    }

    private fun showItemMenu(item: InventoryItem, position: Int, anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.inventory_card_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    showEditDialog(item.name, item.quantity) { newName, newQty ->
                        item.name = newName
                        item.quantity = newQty
                        adapter.notifyItemChanged(position)
                        Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.add -> {
                    val currentQty = item.quantity.toIntOrNull() ?: 0
                    val newQty = currentQty + 1
                    item.quantity = newQty.toString()
                    adapter.notifyItemChanged(position)
                    Toast.makeText(this, "${item.name} quantity: $newQty", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.delete -> {
                    showDeleteConfirmationDialog(item, position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAddItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val itemName = dialogView.findViewById<EditText>(R.id.editItemName)
        val itemQuantity = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        val btnAddItem = dialogView.findViewById<Button>(R.id.btn_add_item)
        val btnCancelAddItem = dialogView.findViewById<Button>(R.id.btn_cancel_add_item)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        selectImageButton.setOnClickListener {
            Toast.makeText(this, "Image selection is not implemented yet", Toast.LENGTH_SHORT).show()
        }

        btnAddItem.setOnClickListener { _ ->
            val name = itemName.text.toString()
            val quantity = itemQuantity.text.toString()

            if (name.isNotEmpty() && quantity.isNotEmpty()) {
                val newItem = InventoryItem(name, quantity, R.drawable.img_placeholder)
                when (selectedTab) {
                    0 -> fabricData.add(newItem)
                    1 -> colorData.add(newItem)
                    2 -> closuresData.add(newItem)
                    3 -> accentsData.add(newItem)
                }
                updateDataForSelectedTab()
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
        currentName: String,
        currentQuantity: String,
        onSave: (String, String) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_item, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.editName)
        val quantityInput = dialogView.findViewById<EditText>(R.id.editQuantity)

        nameInput.setText(currentName)
        quantityInput.setText(currentQuantity)

        AlertDialog.Builder(this)
            .setTitle("Edit Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newQuantity = quantityInput.text.toString().trim()
                if (newName.isNotEmpty() && newQuantity.isNotEmpty()) {
                    onSave(newName, newQuantity)
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmationDialog(item: InventoryItem, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete \"${item.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                adapter.items.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "${item.name} deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    // endregion
}
