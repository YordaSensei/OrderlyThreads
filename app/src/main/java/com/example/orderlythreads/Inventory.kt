package com.example.orderlythreads

import android.os.Bundle
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inventory)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TABS
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)

        val tabs = listOf(tab1, tab2, tab3, tab4)

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
            }
        }

        // DATA LISTS
        val fabricData = mutableListOf(
            InventoryItem("Merino", "20", R.drawable.img_merino),
            InventoryItem("Souffle Yarn", "20", R.drawable.img_scouffle_yarn),
            InventoryItem("Lambswool", "20", R.drawable.img_lambswool),
            InventoryItem("Cashmere", "20", R.drawable.img_cashmere),
            InventoryItem("Milano Ribbed", "20", R.drawable.img_milano_ribbed),
            InventoryItem("Alpaca", "20", R.drawable.img_alpaca)
        )

        val colorData = mutableListOf(
            InventoryItem("Solid", "10", R.drawable.img_solid_color),
            InventoryItem("Striped", "10", R.drawable.img_striped_color),
            InventoryItem("Checkered", "10", R.drawable.img_checkered)
        )

        val closuresData = mutableListOf(
            InventoryItem("Button", "20", R.drawable.img_button),
            InventoryItem("Zipper", "20", R.drawable.img_zipper)
        )

        val accentsData = mutableListOf(
            InventoryItem("Bows", "20", R.drawable.img_bow),
            InventoryItem("Rivets", "20", R.drawable.img_pearls)
        )

        // RECYCLER + ADAPTER
        lateinit var adapter: InventoryAdapter
        val recycler = findViewById<RecyclerView>(R.id.inventoryRecycler)

        adapter = InventoryAdapter(fabricData) { item, position, anchorView ->
            // Create and show the popup menu
            val popup = PopupMenu(this, anchorView)
            popup.menuInflater.inflate(R.menu.inventory_card_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {

                    R.id.edit -> {
                        // Open dialog to edit item
                        showEditDialog(item.name, item.quantity) { newName, newQty ->
                            adapter.items[position] = item.copy(name = newName, quantity = newQty)
                            adapter.notifyItemChanged(position)
                            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
                        }
                        true
                    }

                    R.id.add -> {
                        // Increment quantity
                        val currentQty = item.quantity.toIntOrNull() ?: 0
                        val newQty = currentQty + 1
                        adapter.items[position] = item.copy(quantity = newQty.toString())
                        adapter.notifyItemChanged(position)
                        Toast.makeText(this, "${item.name} quantity: $newQty", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.delete -> {
                        // Confirm before deleting
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
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

        recycler.adapter = adapter

        // TAB CONTENT SWITCHING
        tab1.setOnClickListener {
            tabs.forEach { it.isSelected = false }
            tab1.isSelected = true
            adapter.updateData(fabricData)
        }

        tab2.setOnClickListener {
            tabs.forEach { it.isSelected = false }
            tab2.isSelected = true
            adapter.updateData(colorData)
        }

        tab3.setOnClickListener {
            tabs.forEach { it.isSelected = false }
            tab3.isSelected = true
            adapter.updateData(closuresData)
        }

        tab4.setOnClickListener {
            tabs.forEach { it.isSelected = false }
            tab4.isSelected = true
            adapter.updateData(accentsData)
        }

        // DEFAULT LOAD
        adapter.updateData(fabricData)
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

}
