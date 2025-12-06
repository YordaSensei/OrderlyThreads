package com.example.orderlythreads

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectDesignAttributes : AppCompatActivity() {

    private lateinit var upperWearInfo: LinearLayout
    private lateinit var lowerWearInfo: LinearLayout
    private lateinit var clientDetailsInfo: LinearLayout
    private lateinit var upperWearSign: ImageView
    private lateinit var lowerWearSign: ImageView
    private lateinit var clientDetailsSign: ImageView

    private lateinit var etClientName: EditText
    private lateinit var etContactInfo: EditText
    private lateinit var etWaist: EditText
    private lateinit var etHips: EditText
    private lateinit var etChest: EditText
    private lateinit var etLength: EditText
    private lateinit var etShoulder: EditText
    private lateinit var etSleeve: EditText
    private lateinit var etNotes: EditText

    private lateinit var btnSaveOrder: Button
    private lateinit var btnAddUpperAccentQty: Button
    private lateinit var btnAddLowerAccentQty: Button

    private var upperAccentQty: Int = 0
    private var lowerAccentQty: Int = 0

    private lateinit var adapterUpperCasual: ImageAdapter
    private lateinit var adapterUpperFormal: ImageAdapter
    private lateinit var adapterUpperFabric: ImageAdapter
    private lateinit var adapterUpperColor: ImageAdapter
    private lateinit var adapterUpperAccents: ImageAdapter
    private lateinit var adapterUpperAccentColors: ImageAdapter
    private lateinit var adapterLowerCasual: ImageAdapter
    private lateinit var adapterLowerFormal: ImageAdapter
    private lateinit var adapterLowerFabric: ImageAdapter
    private lateinit var adapterLowerColor: ImageAdapter
    private lateinit var adapterLowerAccents: ImageAdapter
    private lateinit var adapterLowerAccentColors: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_design_attributes)

        initializeViews()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupAllAdapters()

        btnAddUpperAccentQty.setOnClickListener { showQuantityDialog("Upper Accents") { qty -> upperAccentQty = qty } }
        btnAddLowerAccentQty.setOnClickListener { showQuantityDialog("Lower Accents") { qty -> lowerAccentQty = qty } }
        btnSaveOrder.setOnClickListener { showConfirmationDialog() }
    }

    private fun initializeViews() {
        upperWearInfo = findViewById(R.id.upperWearLayout)
        upperWearSign = findViewById(R.id.upperWearIcon)
        lowerWearInfo = findViewById(R.id.lowerWearLayout)
        lowerWearSign = findViewById(R.id.lowerWearIcon)
        clientDetailsInfo = findViewById(R.id.clientDetailsLayout)
        clientDetailsSign = findViewById(R.id.clientOrderIcon)
        etClientName = findViewById(R.id.etClientName)
        etContactInfo = findViewById(R.id.etContactInfo)
        etWaist = findViewById(R.id.waistMeasurement)
        etHips = findViewById(R.id.hipMeasurement)
        etChest = findViewById(R.id.chestMeasurement)
        etLength = findViewById(R.id.lengthMeasurement)
        etShoulder = findViewById(R.id.shoulderMeasurement)
        etSleeve = findViewById(R.id.sleeveMeasurement)
        etNotes = findViewById(R.id.notesDetails)
        btnSaveOrder = findViewById(R.id.btnSubmitOrder)
        btnAddUpperAccentQty = findViewById(R.id.addUpperAccentsBtn)
        btnAddLowerAccentQty = findViewById(R.id.addLowerAccentsBtn)
    }

    // In SelectDesignAttributes.kt

    private fun setupAllAdapters() {
        // --- Image Data Lists and Name Data Lists (no changes here) ---
        val casualUpperImages = listOf(R.drawable.casual_upper_t_shirt, R.drawable.casual_upper_polo_shirt, R.drawable.casual_upper_hoodie, R.drawable.casual_upper_sweatshirt, R.drawable.casual_upper_sleeveless_shirt, R.drawable.casual_upper_dress)
        val formalUpperImages = listOf(R.drawable.formal_upper_dress_shirt, R.drawable.formal_upper_suit, R.drawable.formal_upper_dress, R.drawable.formal_upper_gown, R.drawable.formal_upper_barong, R.drawable.formal_upper_filipiniana)
        val casualLowerImages = listOf(R.drawable.casual_lower_jeans, R.drawable.casual_lower_shorts, R.drawable.casual_lower_cargo_pants, R.drawable.casual_lower_sweatpants, R.drawable.casual_lower_leggings, R.drawable.casual_lower_skirt)
        val formalLowerImages = listOf(R.drawable.formal_lower_dress_pants, R.drawable.formal_lower_wide_chinos, R.drawable.formal_lower_pencil_skirt, R.drawable.formal_lower_full_length_skirt, R.drawable.formal_lower_pleated_trousers, R.drawable.formal_lower_wide_trousers)
        val casualUpperNames = listOf("T-Shirt", "Polo Shirt", "Hoodie", "Sweatshirt", "Sleeveless Shirt", "Dress")
        val formalUpperNames = listOf("Dress Shirt", "Suit", "Formal Dress", "Gown", "Barong", "Filipiniana")
        val casualLowerNames = listOf("Jeans", "Shorts", "Cargo Pants", "Sweatpants", "Leggings", "Skirt")
        val formalLowerNames = listOf("Dress Pants", "Wide Chinos", "Pencil Skirt", "Full-Length Skirt", "Pleated Trousers", "Wide Trousers")
        val upperFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_design_test }) }
        val upperFabricNames = listOf("Search Fabric") + (1..12).map { "Fabric #$it" }
        val upperColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val upperColorNames = listOf("Search Color") + (1..12).map { "Color #$it" }
        val upperAccentImages = List(12) { R.drawable.sample_design_test }
        val upperAccentNames = (1..12).map { "Accent #$it" }
        val upperAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val upperAccentColorNames = listOf("Search Accent Color") + (1..12).map { "Accent Color #$it" }
        val lowerFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_design_test }) }
        val lowerFabricNames = listOf("Search Fabric") + (1..12).map { "Fabric #$it" }
        val lowerColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val lowerColorNames = listOf("Search Color") + (1..12).map { "Color #$it" }
        val lowerAccentImages = List(12) { R.drawable.sample_design_test }
        val lowerAccentNames = (1..12).map { "Accent #$it" }
        val lowerAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val lowerAccentColorNames = listOf("Search Accent Color") + (1..12).map { "Accent Color #$it" }

        // --- Adapter Initialization (WITH NAMED ARGUMENTS) ---

        // Upper Wear Adapters
        adapterUpperCasual = ImageAdapter(this, casualUpperImages, R.layout.item_garment_card, nameList = casualUpperNames) { position ->
            adapterUpperCasual.setSelection(position)
            if (::adapterUpperFormal.isInitialized) adapterUpperFormal.clearSelection()
        }
        adapterUpperFormal = ImageAdapter(this, formalUpperImages, R.layout.item_garment_card, nameList = formalUpperNames) { position ->
            adapterUpperFormal.setSelection(position)
            if (::adapterUpperCasual.isInitialized) adapterUpperCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvUpperCasual, adapterUpperCasual)
        attachAdapterToView(R.id.rvUpperFormal, adapterUpperFormal)

        // Lower Wear Adapters
        adapterLowerCasual = ImageAdapter(this, casualLowerImages, R.layout.item_garment_card, nameList = casualLowerNames) { position ->
            adapterLowerCasual.setSelection(position)
            if (::adapterLowerFormal.isInitialized) adapterLowerFormal.clearSelection()
        }
        adapterLowerFormal = ImageAdapter(this, formalLowerImages, R.layout.item_garment_card, nameList = formalLowerNames) { position ->
            adapterLowerFormal.setSelection(position)
            if (::adapterLowerCasual.isInitialized) adapterLowerCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvLowerCasual, adapterLowerCasual)
        attachAdapterToView(R.id.rvLowerFormal, adapterLowerFormal)

        // Browsable and Accent Adapters
        adapterUpperFabric = setupRecyclerView(R.id.rvUpperFabrics, upperFabricImages, upperFabricNames, R.layout.item_fabric_card)
        adapterUpperColor = setupRecyclerView(R.id.rvUpperColors, upperColorImages, upperColorNames, R.layout.item_color_circle)
        adapterUpperAccents = setupRecyclerView(R.id.rvUpperAccents, upperAccentImages, upperAccentNames, R.layout.item_garment_card)
        adapterUpperAccentColors = setupRecyclerView(R.id.rvUpperAccentsColors, upperAccentColorImages, upperAccentColorNames, R.layout.item_color_circle)

        adapterLowerFabric = setupRecyclerView(R.id.rvLowerFabrics, lowerFabricImages, lowerFabricNames, R.layout.item_fabric_card)
        adapterLowerColor = setupRecyclerView(R.id.rvLowerColors, lowerColorImages, lowerColorNames, R.layout.item_color_circle)
        adapterLowerAccents = setupRecyclerView(R.id.rvLowerAccents, lowerAccentImages, lowerAccentNames, R.layout.item_garment_card)
        adapterLowerAccentColors = setupRecyclerView(R.id.rvLowerAccentColors, lowerAccentColorImages, lowerAccentColorNames, R.layout.item_color_circle)
    }

    // UPDATE this function to accept and pass the name list
    private fun setupRecyclerView(recyclerViewId: Int, data: List<Int>, names: List<String>, itemLayoutId: Int): ImageAdapter {
        lateinit var adapter: ImageAdapter
        val clickCallback = { position: Int ->
            val isBrowsable = data.firstOrNull() == R.drawable.ic_search
            if (isBrowsable && position == 0) {
                showBrowseDialog("Select Item", data, itemLayoutId, adapter)
            } else {
                adapter.setSelection(position)
            }
        }
        // Pass the names list to the adapter
        adapter = ImageAdapter(this, data, itemLayoutId, names, onItemClickCallback = clickCallback)
        attachAdapterToView(recyclerViewId, adapter)
        return adapter
    }


    private fun attachAdapterToView(recyclerViewId: Int, adapter: ImageAdapter) {
        findViewById<RecyclerView>(recyclerViewId)?.apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            (itemAnimator as? androidx.recyclerview.widget.SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }


    private fun showBrowseDialog(title: String, data: List<Int>, layoutId: Int, mainAdapterToUpdate: ImageAdapter) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_grid_selection, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        val rvGrid = dialogView.findViewById<RecyclerView>(R.id.rvGrid)
        tvTitle.text = title

        val gridData = data.drop(1)
        val gridAdapter = ImageAdapter(this, gridData, layoutId, isBrowsable = false, isGridMode = true) { positionInGrid ->
            val positionInMainList = positionInGrid + 1
            mainAdapterToUpdate.setSelection(positionInMainList)
            dialog.dismiss()
        }

        rvGrid.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 3)
        rvGrid.adapter = gridAdapter
        (rvGrid.itemAnimator as? androidx.recyclerview.widget.SimpleItemAnimator)?.supportsChangeAnimations = false

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showQuantityDialog(title: String, onQuantitySet: (Int) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val etQty = dialogView.findViewById<EditText>(R.id.etQuantity)
        val btnOk = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        tvTitle.text = title

        btnOk.setOnClickListener {
            val qty = etQty.text.toString().toIntOrNull()
            if (qty != null) {
                onQuantitySet(qty)
                Toast.makeText(this, "$title set to $qty", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
        btnCancel.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showConfirmationDialog() {
        val upperDesign = if (adapterUpperCasual.selectedPosition != RecyclerView.NO_POSITION) adapterUpperCasual.getSelectionName() else adapterUpperFormal.getSelectionName()
        val lowerDesign = if (adapterLowerCasual.selectedPosition != RecyclerView.NO_POSITION) adapterLowerCasual.getSelectionName() else adapterLowerFormal.getSelectionName()
        val clientName = etClientName.text.toString()
        val summary = """
            <b>Client:</b> $clientName<br/>
            <b>Contact:</b> ${etContactInfo.text}<br/><br/>
            <big><b>Measurements</b></big><br/>
            Waist: ${etWaist.text}, Hips: ${etHips.text}<br/>
            Chest: ${etChest.text}, Length: ${etLength.text}<br/>
            Shoulder: ${etShoulder.text}, Sleeve: ${etSleeve.text}<br/><br/>
            <big><b>Upper Wear Selection</b></big><br/>
            <b>Design:</b> "$upperDesign"<br/>
            <b>Fabric:</b> ${adapterUpperFabric.getSelectionName()}<br/>
            <b>Color:</b> ${adapterUpperColor.getSelectionName()}<br/>
            <b>Accent Design:</b> ${adapterUpperAccents.getSelectionName()}<br/>
            <b>Accent Color:</b> ${adapterUpperAccentColors.getSelectionName()}<br/>
            <b>Accent Quantity:</b> $upperAccentQty<br/><br/>
            <big><b>Lower Wear Selection</b></big><br/>
            <b>Design:</b> "$lowerDesign"<br/>
            <b>Fabric:</b> ${adapterLowerFabric.getSelectionName()}<br/>
            <b>Color:</b> ${adapterLowerColor.getSelectionName()}<br/>
            <b>Accent Design:</b> ${adapterLowerAccents.getSelectionName()}<br/>
            <b>Accent Color:</b> ${adapterLowerAccentColors.getSelectionName()}<br/>
            <b>Accent Quantity:</b> $lowerAccentQty<br/><br/>
            <big><b>Notes</b></big><br/>
            ${etNotes.text}<br/><br/>
            <i>Proceed with this order?</i>
        """.trimIndent()

        val dialogView = layoutInflater.inflate(R.layout.dialog_save_order, null)
        val dialog = AlertDialog.Builder(this).setView(dialogView).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvSummary = dialogView.findViewById<TextView>(R.id.tvOrderSummary)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnEdit = dialogView.findViewById<Button>(R.id.btnEdit)
        tvSummary.text = HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnConfirm.setOnClickListener {
            Toast.makeText(this, "Order Saved Successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnEdit.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun expandUpperWear(view: View) { toggleVisibility(upperWearInfo, upperWearSign) }
    fun expandLowerWear(view: View) { toggleVisibility(lowerWearInfo, lowerWearSign) }
    fun expandClientDetails(view: View) { toggleVisibility(clientDetailsInfo, clientDetailsSign) }

    private fun toggleVisibility(layout: View, icon: ImageView) {
        val isVisible = layout.isVisible
        layout.visibility = if (isVisible) View.GONE else View.VISIBLE
        icon.animate().rotationBy(if (isVisible) -135f else 135f).setDuration(200).start()
    }
}
