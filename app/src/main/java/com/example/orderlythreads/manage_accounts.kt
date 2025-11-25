package com.example.orderlythreads

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class manage_accounts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_accounts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_account_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val addBtn = findViewById<Button>(R.id.addBtn)
        val exitBtn = dialog.findViewById<Button>(R.id.exitBtn)

        addBtn.setOnClickListener { view ->
            dialog.show()
        }

        exitBtn.setOnClickListener { view ->
            dialog.hide()
        }
    }
}