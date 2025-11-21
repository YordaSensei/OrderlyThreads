package com.example.orderlythreads

import android.os.Bundle
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)
        val tab3 = findViewById<TextView>(R.id.tab3)
        val tab4 = findViewById<TextView>(R.id.tab4)

        val tabs =  listOf(tab1, tab2, tab3, tab4)

        //loop that handles selection of each tab
        tabs.forEach { tab ->
            tab.setOnClickListener{

                tabs.forEach { it.isSelected  = false } //deselects the current tab open

                tab.isSelected = true //highlights the selected tab
            }

        }

        //Code for loading data
        val cardFrames = listOf(
            findViewById<FrameLayout>(R.id.fabricCard1),
            findViewById(R.id.fabricCard2),
            findViewById(R.id.fabricCard3),
            findViewById(R.id.fabricCard4),
            findViewById(R.id.fabricCard5),
            findViewById(R.id.fabricCard6)
        )

        val cardImages = listOf(
            findViewById<ImageView>(R.id.fabricImage1),
            findViewById(R.id.fabricImage2),
            findViewById(R.id.fabricImage3),
            findViewById(R.id.fabricImage4),
            findViewById(R.id.fabricImage5),
            findViewById(R.id.fabricImage6)
        )

        val cardNames = listOf(
            findViewById<TextView>(R.id.fabricName1),
            findViewById(R.id.fabricName2),
            findViewById(R.id.fabricName3),
            findViewById(R.id.fabricName4),
            findViewById(R.id.fabricName5),
            findViewById(R.id.fabricName6)
        )

        val cardQuantities = listOf(
            findViewById<TextView>(R.id.fabricQuantity1),
            findViewById(R.id.fabricQuantity2),
            findViewById(R.id.fabricQuantity3),
            findViewById(R.id.fabricQuantity4),
            findViewById(R.id.fabricQuantity5),
            findViewById(R.id.fabricQuantity6)
        )

        val cardMenus = listOf(
            findViewById<ImageView>(R.id.fabricMenu1),
            findViewById(R.id.fabricMenu2),
            findViewById(R.id.fabricMenu3),
            findViewById(R.id.fabricMenu4),
            findViewById(R.id.fabricMenu5),
            findViewById(R.id.fabricMenu6)
        )

        val fabricData = listOf(
            Triple("Merino", "0", R.drawable.merino),
            Triple("Souffle Yarn", "0", R.drawable.scouffle_yarn),
            Triple("Lambswool", "0", R.drawable.lambswool),
            Triple("Cashmere", "0", R.drawable.cashmere),
            Triple("Milano Ribbed", "0", R.drawable.milano_ribbed),
            Triple("Alpaca", "0", R.drawable.alpaca)
        )

        val colorData = listOf(
            Triple("Solid", "0", R.drawable.ic_placeholder),
            Triple("Striped", "0", R.drawable.ic_placeholder),
            Triple("Checkered", "0", R.drawable.ic_placeholder)
        )

        val materialData = listOf(
            Triple("Cotton", "0", R.drawable.ic_placeholder),
            Triple("Polyester", "0", R.drawable.ic_placeholder)
        )

        val accentsData = listOf(
            Triple("Buttons", "0", R.drawable.ic_placeholder),
            Triple("Zippers", "0", R.drawable.ic_placeholder),
            Triple("Rivets", "0", R.drawable.ic_placeholder)
        )



        fun loadCards(list: List<Triple<String, String, Int>>) {

            // Loop through all 6 card slots
            for (i in cardFrames.indices) {

                if (i < list.size) {
                    // Card needs visible content
                    val (name, qty, imgRes) = list[i]

                    cardFrames[i].visibility = View.VISIBLE
                    cardNames[i].text = name
                    cardQuantities[i].text = "Item Quantity: $qty"
                    cardImages[i].setImageResource(imgRes)

                    // Setup popup menu for each card
                    cardMenus[i].setOnClickListener { view ->
                        val popup = PopupMenu(this, view)
                        popup.menuInflater.inflate(R.menu.fabric_card_menu, popup.menu)
                        popup.setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> {
                                    // TODO: Add edit logic
                                    true
                                }
                                R.id.add -> {
                                    // TODO: Add logic
                                    true
                                }
                                R.id.delete -> {
                                    // TODO: Delete logic
                                    true
                                }
                                else -> false
                            }
                        }
                        popup.show()
                    }

                } else {
                    // Card slot has no data
                    cardFrames[i].visibility = View.GONE
                }
            }
        }


        tab1.setOnClickListener {
            loadCards(fabricData)
        }

        tab2.setOnClickListener {
            loadCards(colorData)
        }

        tab3.setOnClickListener {
            loadCards(materialData)
        }

        tab4.setOnClickListener {
            loadCards(accentsData)
        }


        // DEFAULT TAB
        loadCards(fabricData)


    }
}