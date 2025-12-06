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
import androidx.activity.result.launch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.Orders
import com.example.orderlythreads.Database.OrdersViewModel
import kotlin.text.format

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
    private lateinit var etOrderDate: EditText
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
    private lateinit var ordersViewModel: OrdersViewModel

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

        etOrderDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            android.app.DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Month is 0-indexed, so we add 1
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etOrderDate.setText(formattedDate)
                },
                year,
                month,
                day
            ).show()
        }

        btnAddUpperAccentQty.setOnClickListener { showQuantityDialog("Upper Accents") { qty -> upperAccentQty = qty } }
        btnAddLowerAccentQty.setOnClickListener { showQuantityDialog("Lower Accents") { qty -> lowerAccentQty = qty } }
        btnSaveOrder.setOnClickListener { showConfirmationDialog() }

        val etOrderDate = findViewById<EditText>(R.id.etOrderDate)
        ordersViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
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
        etOrderDate = findViewById(R.id.etOrderDate)
        etNotes = findViewById(R.id.notesDetails)
        btnSaveOrder = findViewById(R.id.btnSubmitOrder)
        btnAddUpperAccentQty = findViewById(R.id.addUpperAccentsBtn)
        btnAddLowerAccentQty = findViewById(R.id.addLowerAccentsBtn)
    }

    private fun setupAllAdapters() {
        val casualUpperImages = listOf(R.drawable.sample_design_test, R.drawable.sample_design_test, R.drawable.sample_design_test, R.drawable.sample_design_test)
        val formalUpperImages = listOf(R.drawable.sample_design_test, R.drawable.sample_design_test, R.drawable.sample_design_test, R.drawable.sample_design_test)
        val upperFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_design_test }) }
        val upperColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val upperAccentImages = List(12) { R.drawable.sample_design_test }
        val upperAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val casualLowerImages = List(6) { R.drawable.sample_design_test }
        val formalLowerImages = List(6) { R.drawable.sample_design_test }
        val lowerFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_design_test }) }
        val lowerColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val lowerAccentImages = List(12) { R.drawable.sample_design_test }
        val lowerAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }

        adapterUpperCasual = ImageAdapter(this, casualUpperImages, R.layout.item_garment_card) { position ->
            adapterUpperCasual.setSelection(position)
            if (::adapterUpperFormal.isInitialized) adapterUpperFormal.clearSelection()
        }
        adapterUpperFormal = ImageAdapter(this, formalUpperImages, R.layout.item_garment_card) { position ->
            adapterUpperFormal.setSelection(position)
            if (::adapterUpperCasual.isInitialized) adapterUpperCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvUpperCasual, adapterUpperCasual)
        attachAdapterToView(R.id.rvUpperFormal, adapterUpperFormal)

        adapterLowerCasual = ImageAdapter(this, casualLowerImages, R.layout.item_garment_card) { position ->
            adapterLowerCasual.setSelection(position)
            if (::adapterLowerFormal.isInitialized) adapterLowerFormal.clearSelection()
        }
        adapterLowerFormal = ImageAdapter(this, formalLowerImages, R.layout.item_garment_card) { position ->
            adapterLowerFormal.setSelection(position)
            if (::adapterLowerCasual.isInitialized) adapterLowerCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvLowerCasual, adapterLowerCasual)
        attachAdapterToView(R.id.rvLowerFormal, adapterLowerFormal)

        adapterUpperFabric = setupRecyclerView(R.id.rvUpperFabrics, upperFabricImages, R.layout.item_fabric_card)
        adapterUpperColor = setupRecyclerView(R.id.rvUpperColors, upperColorImages, R.layout.item_color_circle)
        adapterUpperAccents = setupRecyclerView(R.id.rvUpperAccents, upperAccentImages, R.layout.item_garment_card)
        adapterUpperAccentColors = setupRecyclerView(R.id.rvUpperAccentsColors, upperAccentColorImages, R.layout.item_color_circle)
        adapterLowerFabric = setupRecyclerView(R.id.rvLowerFabrics, lowerFabricImages, R.layout.item_fabric_card)
        adapterLowerColor = setupRecyclerView(R.id.rvLowerColors, lowerColorImages, R.layout.item_color_circle)
        adapterLowerAccents = setupRecyclerView(R.id.rvLowerAccents, lowerAccentImages, R.layout.item_garment_card)
        adapterLowerAccentColors = setupRecyclerView(R.id.rvLowerAccentColors, lowerAccentColorImages, R.layout.item_color_circle)

        // --- 1. GARMENTS ---
        // Using R.drawable.your_image_name
        val garmentList = listOf(
            com.example.orderlythreads.Models.DesignOption("Shirt", R.drawable.ic_shirt), // Replace 'ic_shirt' with your actual file name
            com.example.orderlythreads.Models.DesignOption("Pants", R.drawable.ic_pants),
            com.example.orderlythreads.Models.DesignOption("Dress", R.drawable.ic_dress),
            com.example.orderlythreads.Models.DesignOption("Skirt", R.drawable.ic_skirt)
        )

        rvGarments.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        rvGarments.adapter = com.example.orderlythreads.Adapters.DesignOptionAdapter(garmentList) { name ->
            selectedGarment = name
        }

        // --- 2. FABRICS ---
        // Even if you load these later, here is how you hardcode them for now so images show up
        val fabricList = listOf(
            com.example.orderlythreads.Models.DesignOption("Cotton", R.drawable.fabric_cotton),
            com.example.orderlythreads.Models.DesignOption("Silk", R.drawable.fabric_silk),
            com.example.orderlythreads.Models.DesignOption("Linen", R.drawable.fabric_linen),
            com.example.orderlythreads.Models.DesignOption("Wool", R.drawable.fabric_wool)
        )

        rvFabrics.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        rvFabrics.adapter = com.example.orderlythreads.Adapters.DesignOptionAdapter(fabricList) { name ->
            selectedFabric = name
        }

        // --- 3. COLORS / ACCENTS ---
        // You need small square/circle images for colors (e.g., color_red.png)
        val colorList = listOf(
            com.example.orderlythreads.Models.DesignOption("Red", R.drawable.color_red),
            com.example.orderlythreads.Models.DesignOption("Blue", R.drawable.color_blue),
            com.example.orderlythreads.Models.DesignOption("Green", R.drawable.color_green),
            com.example.orderlythreads.Models.DesignOption("Black", R.drawable.color_black)
        )

        // Setup Main Colors
        rvColors.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        rvColors.adapter = com.example.orderlythreads.Adapters.DesignOptionAdapter(colorList) { name ->
            selectedColor = name
        }

        // Setup Upper Accents (Reusing the color list)
        rvUpperAccent.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        rvUpperAccent.adapter = com.example.orderlythreads.Adapters.DesignOptionAdapter(colorList) { name ->
            selectedUpperAccent = name
        }

        // Setup Lower Accents (Reusing the color list)
        rvLowerAccent.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        rvLowerAccent.adapter = com.example.orderlythreads.Adapters.DesignOptionAdapter(colorList) { name ->
            selectedLowerAccent = name
        }
    }

    private fun attachAdapterToView(recyclerViewId: Int, adapter: ImageAdapter) {
        findViewById<RecyclerView>(recyclerViewId)?.apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            (itemAnimator as? androidx.recyclerview.widget.SimpleItemAnimator)?.supportsChangeAnimations = false
        }
    }

    private fun setupRecyclerView(recyclerViewId: Int, data: List<Int>, itemLayoutId: Int): ImageAdapter {
        lateinit var adapter: ImageAdapter
        val clickCallback = { position: Int ->
            val isBrowsable = data.firstOrNull() == R.drawable.ic_search
            if (isBrowsable && position == 0) {
                showBrowseDialog("Select Item", data, itemLayoutId, adapter)
            } else {
                adapter.setSelection(position)
            }
        }
        adapter = ImageAdapter(this, data, itemLayoutId, onItemClickCallback = clickCallback)
        attachAdapterToView(recyclerViewId, adapter)
        return adapter
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
            saveOrderToDatabase()
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

    private fun saveOrderToDatabase() {
        // --- Gather all data ---
        val name = etClientName.text.toString()
        val contact = etContactInfo.text.toString()
        val selectedDueDate = etOrderDate.text.toString()
        val notes = etNotes.text.toString()
        val totalQuantity = if ((upperAccentQty + lowerAccentQty) > 0) (upperAccentQty + lowerAccentQty) else 1

        // Helper to get text safely
        fun getVal(id: Int): String = findViewById<EditText>(id).text.toString()

        // --- Validation ---
        if (name.isEmpty() || selectedDueDate.isEmpty()) {
            Toast.makeText(this, "Please enter Client Name and Due Date", Toast.LENGTH_SHORT).show()
            return
        }

        // --- Date Formatting ---
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val currentDate = sdf.format(java.util.Date())

        // --- Create the Order object ---
        val newOrder = Orders(
            clientName = name,
            contact = contact,
            orderDate = currentDate,
            dueDate = selectedDueDate,
            quantity = totalQuantity,
            waist = getVal(R.id.waistMeasurement),
            chest = getVal(R.id.chestMeasurement),
            shoulderWidth = getVal(R.id.shoulderMeasurement),
            sleeveLength = getVal(R.id.sleeveMeasurement),
            armhole = "0", // Placeholder
            neckline = "0", // Placeholder
            garmentLength = getVal(R.id.lengthMeasurement),
            additionalNotes = notes
        )

        // --- Save to database ---
        ordersViewModel.addOrder(newOrder)

        // --- Final confirmation and screen close ---
        Toast.makeText(this, "Order Saved Successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
}
