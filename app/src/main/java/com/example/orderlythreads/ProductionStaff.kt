package com.example.orderlythreads

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductionStaff : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_production_staff)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

            val recyclerview: RecyclerView = findViewById(R.id.recyclerItems)

            // ArrayList of class ItemsViewModel
            val data = ArrayList<ProductionClassItems>()

            // This loop will create 20 Views containing
            // the image with the count of view
            for (i in 1..20) {
                data.add(ProductionClassItems(R.drawable.gray_background, "ProductionClassItems $i"))
            }

            val adapter = ProductionAdapter(data)

            recyclerview.adapter = adapter
            // this creates a vertical layout Manager
            recyclerview.layoutManager = LinearLayoutManager(this)

            val tab1 = findViewById<TextView>(R.id.tab1)
            val tab2 = findViewById<TextView>(R.id.tab2)
            val tab3 = findViewById<TextView>(R.id.tab3)
            val tab4 = findViewById<TextView>(R.id.tab4)

            val tabs = listOf(tab1, tab2, tab3, tab4)

            //loop that handles selection of each tab
            tabs.forEach { tab ->
                tab.setOnClickListener {

                    tabs.forEach { it.isSelected = false } //deselects the current tab open

                    tab.isSelected = true //highlights the selected tab
                }
            }
        }
}
