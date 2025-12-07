package com.example.orderlythreads

import android.app.Activity
import android.app.Dialog
import android.widget.ImageButton
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.orderlythreads.Database.Inventory
import com.example.orderlythreads.Database.InventoryDao
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.InventoryRepository
import com.example.orderlythreads.Database.InventoryViewModel
import com.example.orderlythreads.Database.InventoryViewModelFactory
import java.io.File
import java.io.FileOutputStream
import android.widget.AdapterView

class Inventory : AppCompatActivity() {

    private lateinit var adapter: InventoryAdapter
    private lateinit var inventoryViewModel: InventoryViewModel
    private lateinit var inventoryDao: InventoryDao

    // Category names matching the tabs
    private val categoryNames = listOf("Fabric", "Color/Pattern", "Accents")

    // For Add Item Dialog
    private var currentImageUri: Uri? = null
    private lateinit var imageViewPreview: ImageView

    // For Edit Item Dialog
    private var currentImageUriForEditDialog: Uri? = null
    private lateinit var imageViewPreviewEditDialog: ImageView

    private val imagePickerLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                val imageFile = saveImageToInternalStorage(uri)
                if (imageFile != null) {
                    val localImageUri = Uri.fromFile(imageFile)
                    currentImageUri = localImageUri
                    imageViewPreview.setImageURI(localImageUri)
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val imagePickerLauncherForEdit: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let { uri ->
                val imageFile = saveImageToInternalStorage(uri)
                if (imageFile != null) {
                    val localImageUri = Uri.fromFile(imageFile)
                    currentImageUriForEditDialog = localImageUri
                    imageViewPreviewEditDialog.setImageURI(localImageUri)
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.inventory)

        setupWindowInsets()
        setupLogoutButton()
        setupFooterNavigation()

        inventoryDao = OrderlyThreadsDatabase.getDatabase(applicationContext).inventoryDao()
        val repository = InventoryRepository(inventoryDao)
        val viewModelFactory = InventoryViewModelFactory(repository)
        inventoryViewModel = ViewModelProvider(this, viewModelFactory).get(InventoryViewModel::class.java)

        setupTabs()
        setupRecyclerView()
        setupSearchBar()

        inventoryViewModel.inventoryItems.observe(this) { inventoryList ->
            inventoryList?.let { adapter.updateData(it.toMutableList()) }
        }

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
    
    private fun setupLogoutButton() {
        val logoutBtn = findViewById<View>(R.id.logOutBtn)
        logoutBtn?.setOnClickListener {
            val intent = Intent(this, login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupFooterNavigation() {
        val stockCheckBtn = findViewById<View>(R.id.stockCheckBtn)
        stockCheckBtn?.setOnClickListener {
            val intent = Intent(this, Order_Stock_Check::class.java)
            startActivity(intent)
        }
    }

    private fun setupTabs() {
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)
        val addButton = findViewById<FrameLayout>(R.id.addButton)

        tab3.text = "Accents"

        val tabs = listOf(tab1, tab2, tab3)

        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
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

    private fun setupSearchBar() {
        val searchBar = findViewById<EditText>(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inventoryViewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
        val dialogTitle = dialogView.findViewById<TextView>(R.id.title)
        val itemNameEditText = dialogView.findViewById<EditText>(R.id.editItemName)
        val itemQuantityEditText = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        imageViewPreview = dialogView.findViewById(R.id.imageViewPreview)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        val btnAddItem = dialogView.findViewById<Button>(R.id.btn_add_item)
        val btnCancelAddItem = dialogView.findViewById<Button>(R.id.btn_cancel_add_item)

        imageViewPreview.setImageResource(R.drawable.img_placeholder)
        currentImageUri = null

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        btnAddItem.setOnClickListener { _ ->
            val material = itemNameEditText.text.toString()
            val quantityStr = itemQuantityEditText.text.toString()

            if (material.isNotEmpty() && quantityStr.isNotEmpty()) {
                val quantity = quantityStr.toIntOrNull() ?: 0
                val currentCategory = inventoryViewModel.selectedCategory.value ?: "Unknown"
                val newItem = Inventory(category = currentCategory, material = material, quantity = quantity, imageUri = currentImageUri?.toString())
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


    private fun saveImageToInternalStorage(uri: Uri): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val imagesDir = File(filesDir, "images")
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val outputFile = File(imagesDir, "${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(outputFile)

            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showEditDialog(
        item: Inventory,
        onSave: (Inventory) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_item, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.title)
        val itemNameEditText = dialogView.findViewById<EditText>(R.id.editItemName)
        val itemQuantityEditText = dialogView.findViewById<EditText>(R.id.editItemQuantity)
        imageViewPreviewEditDialog = dialogView.findViewById(R.id.imageViewPreview)
        val selectImageButton = dialogView.findViewById<Button>(R.id.selectImageButton)
        val btnSaveItem = dialogView.findViewById<Button>(R.id.btn_save_item)
        val btnCancelEditItem = dialogView.findViewById<Button>(R.id.btn_cancel_edit_item)

        // Initialize with existing item data
        itemNameEditText.setText(item.material)
        itemQuantityEditText.setText(item.quantity.toString())
        currentImageUriForEditDialog = if (!item.imageUri.isNullOrEmpty()) Uri.parse(item.imageUri) else null

        // Load existing image or placeholder
        if (currentImageUriForEditDialog != null) {
            Glide.with(this)
                .load(currentImageUriForEditDialog)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(imageViewPreviewEditDialog)
        } else {
            imageViewPreviewEditDialog.setImageResource(R.drawable.img_placeholder)
        }

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncherForEdit.launch(intent)
        }

        btnSaveItem.setOnClickListener { _ ->
            val newName = itemNameEditText.text.toString().trim()
            val newQuantityStr = itemQuantityEditText.text.toString().trim()

            if (newName.isNotEmpty() && newQuantityStr.isNotEmpty()) {
                val newQuantity = newQuantityStr.toIntOrNull() ?: 0
                val updatedItem = item.copy(
                    material = newName,
                    quantity = newQuantity,
                    imageUri = currentImageUriForEditDialog?.toString()
                )
                onSave(updatedItem)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelEditItem.setOnClickListener { _ ->
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteConfirmationDialog(item: Inventory) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_item, null)
        val deleteMessage = dialogView.findViewById<TextView>(R.id.delete_message)
        val btnDeleteYes = dialogView.findViewById<Button>(R.id.btn_delete_yes)
        val btnDeleteNo = dialogView.findViewById<Button>(R.id.btn_delete_no)

        deleteMessage.text = "Are you sure you want to delete \"${item.material}\"?"

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        btnDeleteYes.setOnClickListener {
            inventoryViewModel.deleteItem(item)
            Toast.makeText(this, "${item.material} deleted", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnDeleteNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    // endregion
}