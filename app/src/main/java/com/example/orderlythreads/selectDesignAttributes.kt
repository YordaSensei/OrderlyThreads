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

class selectDesignAttributes : AppCompatActivity() {

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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val casualUpperImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val formalUpperImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val upperFabricImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test
        )
        val upperColorImages = listOf(
            R.drawable.sample_color, R.drawable.sample_color,
            R.drawable.sample_color, R.drawable.sample_color, R.drawable.sample_color
        )
        val upperAccentImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val upperAccentColorImages = listOf(
            R.drawable.sample_color, R.drawable.sample_color,
            R.drawable.sample_color, R.drawable.sample_color, R.drawable.sample_color
        )
        val casualLowerImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val formalLowerImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val lowerFabricImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test
        )
        val lowerColorImages = listOf(
            R.drawable.sample_color, R.drawable.sample_color,
            R.drawable.sample_color, R.drawable.sample_color, R.drawable.sample_color
        )
        val lowerAccentImages = listOf(
            R.drawable.sample_design_test, R.drawable.sample_design_test,
            R.drawable.sample_design_test, R.drawable.sample_design_test
        )
        val lowerAccentColorImages = listOf(
            R.drawable.sample_color, R.drawable.sample_color,
            R.drawable.sample_color, R.drawable.sample_color, R.drawable.sample_color
        )

        adapterUpperCasual = ImageAdapter(this, casualUpperImages, R.layout.item_garment_card) {
            if (::adapterUpperFormal.isInitialized) adapterUpperFormal.clearSelection()
        }

        adapterUpperFormal = ImageAdapter(this, formalUpperImages, R.layout.item_garment_card) {
            if (::adapterUpperCasual.isInitialized) adapterUpperCasual.clearSelection()
        }

        attachAdapterToView(R.id.rvUpperCasual, adapterUpperCasual)
        attachAdapterToView(R.id.rvUpperFormal, adapterUpperFormal)

        adapterLowerCasual = ImageAdapter(this, casualLowerImages, R.layout.item_garment_card) {
            if (::adapterLowerFormal.isInitialized) adapterLowerFormal.clearSelection()
        }

        adapterLowerFormal = ImageAdapter(this, formalLowerImages, R.layout.item_garment_card) {
            if (::adapterLowerCasual.isInitialized) adapterLowerCasual.clearSelection()
        }

        attachAdapterToView(R.id.rvLowerCasual, adapterLowerCasual)
        attachAdapterToView(R.id.rvLowerFormal, adapterLowerFormal)

        adapterUpperFabric = setupRecyclerView(R.id.rvUpperFabrics, upperFabricImages, R.layout.item_fabric_card)
        adapterUpperColor = setupRecyclerView(R.id.rvUpperColors, upperColorImages, R.layout.item_color_circle)
        adapterUpperAccents = setupRecyclerView(R.id.rvUpperAccents, upperAccentImages, R.layout.item_design_card)
        adapterUpperAccentColors = setupRecyclerView(R.id.rvUpperAccentsColors, upperAccentColorImages, R.layout.item_color_circle)

        adapterLowerFabric = setupRecyclerView(R.id.rvLowerFabrics, lowerFabricImages, R.layout.item_fabric_card)
        adapterLowerColor = setupRecyclerView(R.id.rvLowerColors, lowerColorImages, R.layout.item_color_circle)
        adapterLowerAccents = setupRecyclerView(R.id.rvLowerAccents, lowerAccentImages, R.layout.item_design_card)
        adapterLowerAccentColors = setupRecyclerView(R.id.rvLowerAccentColors, lowerAccentColorImages, R.layout.item_color_circle)

        btnAddUpperAccentQty.setOnClickListener {
            showQuantityDialog("Upper Accents") { qty ->
                upperAccentQty = qty
            }
        }

        btnAddLowerAccentQty.setOnClickListener {
            showQuantityDialog("Lower Accents") { qty ->
                lowerAccentQty = qty
            }
        }

        btnSaveOrder.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun attachAdapterToView(recyclerViewId: Int, adapter: ImageAdapter) {
        val recyclerView = findViewById<RecyclerView>(recyclerViewId)
        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter
            if (recyclerView.itemAnimator is androidx.recyclerview.widget.SimpleItemAnimator) {
                (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
    }

    private fun setupRecyclerView(recyclerViewId: Int, data: List<Int>, itemLayoutId: Int): ImageAdapter {
        val adapter = ImageAdapter(this, data, itemLayoutId)
        attachAdapterToView(recyclerViewId, adapter)
        return adapter
    }

    private fun showQuantityDialog(title: String, onQuantitySet: (Int) -> Unit) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val tvMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val etQty = dialogView.findViewById<EditText>(R.id.etQuantity)
        val btnOk = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        tvTitle.text = title
        tvMessage.text = "How many accents would you like to add?"

        btnOk.setOnClickListener {
            val qtyString = etQty.text.toString()
            if (qtyString.isNotEmpty()) {
                val qty = qtyString.toInt()
                onQuantitySet(qty)
                Toast.makeText(this, "$title set to $qty", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showConfirmationDialog() {
        val upperDesign = if (adapterUpperCasual.selectedPosition != RecyclerView.NO_POSITION) {
            adapterUpperCasual.getSelectionName()
        } else {
            adapterUpperFormal.getSelectionName()
        }

        val lowerDesign = if (adapterLowerCasual.selectedPosition != RecyclerView.NO_POSITION) {
            adapterLowerCasual.getSelectionName()
        } else {
            adapterLowerFormal.getSelectionName()
        }

        val clientName = etClientName.text.toString()

        val summary = """
            <b>Client:</b> $clientName<br/>
            <b>Contact:</b> ${etContactInfo.text}<br/>
            <br/>
            
            <big><b>Measurements</b></big><br/>
            Waist: ${etWaist.text}, Hips: ${etHips.text}<br/>
            Chest: ${etChest.text}, Length: ${etLength.text}<br/>
            Shoulder: ${etShoulder.text}, Sleeve: ${etSleeve.text}<br/>
            <br/>
            
            <big><b>Upper Wear Selection</b></big><br/>
            <b>Design:</b> "$upperDesign"<br/>
            <b>Fabric:</b> ${adapterUpperFabric.getSelectionName()}<br/>
            <b>Color:</b> ${adapterUpperColor.getSelectionName()}<br/>
            <b>Accent Design:</b> ${adapterUpperAccents.getSelectionName()}<br/>
            <b>Accent Color:</b> ${adapterUpperAccentColors.getSelectionName()}<br/>
            <b>Accent Quantity:</b> $upperAccentQty<br/>
            <br/>
            
            <big><b>Lower Wear Selection</b></big><br/>
            <b>Design:</b> "$lowerDesign"<br/>
            <b>Fabric:</b> ${adapterLowerFabric.getSelectionName()}<br/>
            <b>Color:</b> ${adapterLowerColor.getSelectionName()}<br/>
            <b>Accent Design:</b> ${adapterLowerAccents.getSelectionName()}<br/>
            <b>Accent Color:</b> ${adapterLowerAccentColors.getSelectionName()}<br/>
            <b>Accent Quantity:</b> $lowerAccentQty<br/>
            <br/>
            
            <big><b>Notes</b></big><br/>
            ${etNotes.text}<br/>
            <br/>
            <i>Proceed with this order?</i>
        """.trimIndent()

        val dialogView = layoutInflater.inflate(R.layout.dialog_save_order, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvSummary = dialogView.findViewById<TextView>(R.id.tvOrderSummary)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnEdit = dialogView.findViewById<Button>(R.id.btnEdit)

        tvSummary.text = HtmlCompat.fromHtml(summary, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnConfirm.setOnClickListener {
            Toast.makeText(this, "Order Saved Successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnEdit.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun expandUpperWear(view: View) { toggleVisibility(upperWearInfo, upperWearSign) }
    fun expandLowerWear(view: View) { toggleVisibility(lowerWearInfo, lowerWearSign) }
    fun expandClientDetails(view: View) { toggleVisibility(clientDetailsInfo, clientDetailsSign) }

    private fun toggleVisibility(layout: View, icon: ImageView) {
        val isVisible = layout.isVisible
        layout.visibility = if (isVisible) View.GONE else View.VISIBLE
        icon.setImageResource(if (isVisible) R.drawable.icon_plus else R.drawable.icon_minus)
    }
}