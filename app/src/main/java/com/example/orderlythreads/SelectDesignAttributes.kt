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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.Orders
import com.example.orderlythreads.Database.OrdersRepository
import com.example.orderlythreads.Database.OrdersViewModel
import com.example.orderlythreads.Database.OrdersViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

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
    private lateinit var adapterUpperColor: ColorAdapter
    private lateinit var adapterUpperAccents: ImageAdapter
    private lateinit var adapterUpperAccentColors: ColorAdapter
    private lateinit var adapterLowerCasual: ImageAdapter
    private lateinit var adapterLowerFormal: ImageAdapter
    private lateinit var adapterLowerFabric: ImageAdapter
    private lateinit var adapterLowerColor: ColorAdapter
    private lateinit var adapterLowerAccents: ImageAdapter
    private lateinit var adapterLowerAccentColors: ColorAdapter

    private lateinit var ordersViewModel: OrdersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_design_attributes)

        val database = OrderlyThreadsDatabase.getDatabase(this)
        val repository = OrdersRepository(database.ordersDao())
        val factory = OrdersViewModelFactory(repository)
        ordersViewModel =
            ViewModelProvider(this, factory).get(OrdersViewModel::class.java)

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
        val upperFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.img_placeholder }) }
        val upperFabricNames = listOf("Search Fabric") + (1..12).map { "Fabric #$it" }
        val upperColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val upperColorNames = listOf("Search Color") + (1..12).map { "Color #$it" }
        val upperAccentImages = List(12) { R.drawable.img_placeholder }
        val upperAccentNames = (1..12).map { "Accent #$it" }
        val upperAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val upperAccentColorNames = listOf("Search Accent Color") + (1..12).map { "Accent Color #$it" }
        val lowerFabricImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.img_placeholder }) }
        val lowerFabricNames = listOf("Search Fabric") + (1..12).map { "Fabric #$it" }
        val lowerColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val lowerColorNames = listOf("Search Color") + (1..12).map { "Color #$it" }
        val lowerAccentImages = List(12) { R.drawable.img_placeholder }
        val lowerAccentNames = (1..12).map { "Accent #$it" }
        val lowerAccentColorImages = mutableListOf(R.drawable.ic_search).apply { addAll(List(12) { R.drawable.sample_color }) }
        val lowerAccentColorNames = listOf("Search Accent Color") + (1..12).map { "Accent Color #$it" }

        val colorList = listOf(
            android.graphics.Color.BLACK,
            android.graphics.Color.DKGRAY,
            android.graphics.Color.GRAY,
            android.graphics.Color.LTGRAY,
            android.graphics.Color.WHITE,
            android.graphics.Color.RED,
            "#800000".toColorInt(), // Maroon
            android.graphics.Color.BLUE,
            "#000080".toColorInt(), // Navy
            android.graphics.Color.CYAN,
            android.graphics.Color.GREEN,
            android.graphics.Color.YELLOW,
            android.graphics.Color.MAGENTA
        )

        // Upper Wear Adapters
        adapterUpperCasual = ImageAdapter(
            this,
            casualUpperImages,
            R.layout.item_garment_card,
            nameList = casualUpperNames  // <--- Pass the names here!
        ) { position ->
            adapterUpperCasual.setSelection(position)
            if (::adapterUpperFormal.isInitialized) adapterUpperFormal.clearSelection()
        }
        adapterUpperFormal = ImageAdapter(
            this,
            formalUpperImages,
            R.layout.item_garment_card,
            nameList = formalUpperNames // <--- Pass it here
        ) { position ->
            adapterUpperFormal.setSelection(position)
            if (::adapterUpperCasual.isInitialized) adapterUpperCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvUpperCasual, adapterUpperCasual)
        attachAdapterToView(R.id.rvUpperFormal, adapterUpperFormal)

        // Lower Wear Adapters
        adapterLowerCasual = ImageAdapter(
            this,
            casualLowerImages,
            R.layout.item_garment_card,
            nameList = casualLowerNames // <--- Pass the names here!
            ) { position ->
            adapterLowerCasual.setSelection(position)
            // Clear the other category if selected
            if (::adapterLowerFormal.isInitialized) adapterLowerFormal.clearSelection()
        }
        adapterLowerFormal = ImageAdapter(
            this,
            formalLowerImages,
            R.layout.item_garment_card,
            nameList = formalLowerNames // <--- Pass the names here!
            ) { position ->
            adapterLowerFormal.setSelection(position)
            // Clear the other category if selected
            if (::adapterLowerCasual.isInitialized) adapterLowerCasual.clearSelection()
        }
        attachAdapterToView(R.id.rvLowerCasual, adapterLowerCasual)
        attachAdapterToView(R.id.rvLowerFormal, adapterLowerFormal)

        // Browsable and Accent Adapters
        adapterUpperFabric = setupRecyclerView(R.id.rvUpperFabrics, upperFabricImages, upperFabricNames, R.layout.item_fabric_card)
        adapterUpperColor = ColorAdapter(this, colorList) { selectedColor ->
            // Logic when a color is clicked (Optional: Save to a variable)
            // Example: selectedUpperColor = selectedColor
        }
        findViewById<RecyclerView>(R.id.rvUpperColors).apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterUpperColor
        }
        adapterUpperAccents = setupRecyclerView(R.id.rvUpperAccents, upperAccentImages, upperAccentNames, R.layout.item_garment_card)
        adapterUpperAccentColors = ColorAdapter(this, colorList) { selectedColor ->
            // Logic for accent color
        }
        findViewById<RecyclerView>(R.id.rvUpperAccentsColors).apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterUpperAccentColors
        }

        adapterLowerFabric = setupRecyclerView(R.id.rvLowerFabrics, lowerFabricImages, lowerFabricNames, R.layout.item_fabric_card)
        adapterLowerColor = ColorAdapter(this, colorList) { selectedColor ->
            // Logic when lower color is clicked
        }
        findViewById<RecyclerView>(R.id.rvLowerColors).apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterLowerColor
        }
        adapterLowerAccents = setupRecyclerView(R.id.rvLowerAccents, lowerAccentImages, lowerAccentNames, R.layout.item_garment_card)
        adapterLowerAccentColors = ColorAdapter(this, colorList) { selectedColor ->
            // Logic for accent color
        }
        findViewById<RecyclerView>(R.id.rvLowerAccentColors).apply {
            layoutManager = LinearLayoutManager(this@SelectDesignAttributes, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterLowerAccentColors
        }
    }
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
            <b>Accent Design:</b> ${adapterUpperAccents.getSelectionName()}<br/>
            <b>Accent Quantity:</b> $upperAccentQty<br/><br/>
            <big><b>Lower Wear Selection</b></big><br/>
            <b>Design:</b> "$lowerDesign"<br/>
            <b>Fabric:</b> ${adapterLowerFabric.getSelectionName()}<br/>
            <b>Accent Design:</b> ${adapterLowerAccents.getSelectionName()}<br/>
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
        // 1. Get Client Data
        val name = etClientName.text.toString()
        if (name.isBlank()) {
            Toast.makeText(this, "Please enter Client Name", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Determine Upper Design (Casual vs Formal)
        // If Casual has a selection, use it. Otherwise check Formal.
        var uDesignId = 0
        var uDesignName = "None"

        if (adapterUpperCasual.selectedPosition != RecyclerView.NO_POSITION) {
            uDesignId = adapterUpperCasual.getSelectedResourceId()
            uDesignName = adapterUpperCasual.getSelectionName()
        } else if (adapterUpperFormal.selectedPosition != RecyclerView.NO_POSITION) {
            uDesignId = adapterUpperFormal.getSelectedResourceId()
            uDesignName = adapterUpperFormal.getSelectionName()
        }

        // 3. Determine Lower Design (Casual vs Formal)
        var lDesignId = 0
        if (adapterLowerCasual.selectedPosition != RecyclerView.NO_POSITION) {
            lDesignId = adapterLowerCasual.getSelectedResourceId()
        } else if (adapterLowerFormal.selectedPosition != RecyclerView.NO_POSITION) {
            lDesignId = adapterLowerFormal.getSelectedResourceId()
        }



        // 4. Create the Order Object
        // Note: Ensure your Orders.kt Entity has these matching fields!
        val newOrder = Orders(
            clientName = name,
            contact = etContactInfo.text.toString(),
            orderDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            dueDate = "TBD", // Or add a date picker
            quantity = 1, // Or add a quantity field

            // Measurements
            waist = etWaist.text.toString(),
            hips = etHips.text.toString(),
            chest = etChest.text.toString(),
            length = etLength.text.toString(),
            shoulder = etShoulder.text.toString(),
            sleeve = etSleeve.text.toString(),

            // Upper Selection
            upperDesignId = uDesignId,
            upperFabricId = adapterUpperFabric.getSelectedResourceId(),
            upperColorHex = adapterUpperColor.getSelectedHex(),
            upperAccentDesignId = adapterUpperAccents.getSelectedResourceId(),
            upperAccentColorHex = adapterUpperAccentColors.getSelectedHex(),
            upperAccentQuantity = upperAccentQty,

            // Lower Selection
            lowerDesignId = lDesignId,
            lowerFabricId = adapterLowerFabric.getSelectedResourceId(),
            lowerColorHex = adapterLowerColor.getSelectedHex(),
            lowerAccentDesignId = adapterLowerAccents.getSelectedResourceId(),
            lowerAccentColorHex = adapterLowerAccentColors.getSelectedHex(),
            lowerAccentQuantity = lowerAccentQty,

            additionalNotes = etNotes.text.toString()
        )

        // 5. Save to Database
        ordersViewModel.addOrder(newOrder)

        Toast.makeText(this, "Order Saved to Database!", Toast.LENGTH_LONG).show()
        finish() // Close the screen
    }


//    val logoutBtn = findViewById<ImageButton>(R.id.logOutBtn)
//
//    logoutBtn.setOnClickListener {
//        val intent = Intent(this, login::class.java)
//
//        // Clear history so the user can't go back
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//        startActivity(intent)
//
//        // Close the current screen.
//        finish()
//    }



}
