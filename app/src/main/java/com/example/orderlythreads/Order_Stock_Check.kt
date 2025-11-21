package com.example.orderlythreads

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Order_Stock_Check : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.order_stock_check)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // TABS
        val tab1 = findViewById<TextView>(R.id.tab1)
        val tab2 = findViewById<TextView>(R.id.tab2)

        val tabs = listOf(tab1, tab2)

        tabs.forEach { tab ->
            tab.setOnClickListener {
                tabs.forEach { it.isSelected = false }
                tab.isSelected = true
            }
        }
    }
}