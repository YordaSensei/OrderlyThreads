package com.example.orderlythreads

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        adapterUpperCasual = setupRecyclerView(R.id.rvUpperCasual, casualUpperImages, R.layout.item_garment_card)
        adapterUpperFormal = setupRecyclerView(R.id.rvUpperFormal, formalUpperImages, R.layout.item_garment_card)
        adapterUpperFabric = setupRecyclerView(R.id.rvUpperFabrics, upperFabricImages, R.layout.item_fabric_card)
        adapterUpperColor = setupRecyclerView(R.id.rvUpperColors, upperColorImages, R.layout.item_color_circle)
        adapterUpperAccents = setupRecyclerView(R.id.rvUpperAccents, upperAccentImages, R.layout.item_design_card)
        adapterUpperAccentColors = setupRecyclerView(R.id.rvUpperAccentsColors, upperAccentColorImages, R.layout.item_color_circle)

        adapterLowerCasual = setupRecyclerView(R.id.rvLowerCasual, casualLowerImages, R.layout.item_garment_card)
        adapterLowerFormal = setupRecyclerView(R.id.rvLowerFormal, formalLowerImages, R.layout.item_garment_card)
        adapterLowerFabric = setupRecyclerView(R.id.rvLowerFabrics, lowerFabricImages, R.layout.item_fabric_card)
        adapterLowerColor = setupRecyclerView(R.id.rvLowerColors, lowerColorImages, R.layout.item_color_circle)
        adapterLowerAccents = setupRecyclerView(R.id.rvLowerAccents, lowerAccentImages, R.layout.item_design_card)
        adapterLowerAccentColors = setupRecyclerView(R.id.rvLowerAccentColors, lowerAccentColorImages, R.layout.item_color_circle)

        btnSaveOrder.setOnClickListener {
            showConfirmationDialog()
        }
    }

    private fun setupRecyclerView(recyclerViewId: Int, data: List<Int>, itemLayoutId: Int): ImageAdapter {
        val recyclerView = findViewById<RecyclerView>(recyclerViewId)
        val adapter = ImageAdapter(this, data, itemLayoutId)

        if (recyclerView != null) {
            recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = adapter
            if (recyclerView.itemAnimator is androidx.recyclerview.widget.SimpleItemAnimator) {
                (recyclerView.itemAnimator as androidx.recyclerview.widget.SimpleItemAnimator).supportsChangeAnimations = false
            }
        }
        return adapter
    }

    private fun showConfirmationDialog() {
        val clientName = etClientName.text.toString()

        val summary = """
            Client: $clientName
            Contact: ${etContactInfo.text}
            
            -- Measurements --
            Waist: ${etWaist.text}, Hips: ${etHips.text}
            Chest: ${etChest.text}, Length: ${etLength.text}
            
            -- Upper Wear Selections --
            Casual: ${adapterUpperCasual.getSelectionName()}
            Formal: ${adapterUpperFormal.getSelectionName()}
            Fabric: ${adapterUpperFabric.getSelectionName()}
            Color: ${adapterUpperColor.getSelectionName()}
            Accent: ${adapterUpperAccents.getSelectionName()}
            
            -- Lower Wear Selections --
            Casual: ${adapterLowerCasual.getSelectionName()}
            Formal: ${adapterLowerFormal.getSelectionName()}
            Fabric: ${adapterLowerFabric.getSelectionName()}
            Color: ${adapterLowerColor.getSelectionName()}
            
            Additional Notes: ${etNotes.text}
            
            Proceed with this order?
        """.trimIndent()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Order Details")
        builder.setMessage(summary)

        builder.setPositiveButton("Confirm") { dialog, _ ->
            Toast.makeText(this, "Order Saved Successfully!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        builder.setNegativeButton("Edit") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
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