package com.example.orderlythreads

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.orderlythreads.Database.AccountsViewModel
import com.google.android.material.textfield.TextInputLayout


class login : AppCompatActivity() {
    private lateinit var viewModel: AccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)

        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val emailInput = findViewById<TextInputLayout>(R.id.emailLogin)
        val passwordInput = findViewById<TextInputLayout>(R.id.passwordLogin)

        loginBtn.setOnClickListener {
            val email = emailInput.editText?.text.toString()
            val password = passwordInput.editText?.text.toString()

            viewModel.login(email, password) { account ->

                if (account != null) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

                    if (account.position == "Admin") {
                        startActivity(Intent(this, manage_accounts::class.java))
                    } else if (account.position == "Order Clerk") {
                        startActivity(Intent(this, SelectSampleDesign::class.java))
                    } else if (account.position == "Inventory Manager") {
                        startActivity(Intent(this, Inventory::class.java))
                    }

                    Toast.makeText(this, "Position = ${account.position}", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}