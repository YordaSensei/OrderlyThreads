package com.example.orderlythreads

import SpacingItemDecoration
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.orderlythreads.Database.Accounts
import com.example.orderlythreads.Database.AccountsViewModel
import com.example.orderlythreads.Database.Positions
import com.google.android.material.textfield.TextInputLayout

private lateinit var viewModel: AccountsViewModel

class manage_accounts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        viewModel = ViewModelProvider(this)[AccountsViewModel::class.java]

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_accounts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Dialog Popup Window for Add Account

        val addDialog = Dialog(this)
        addDialog.setContentView(R.layout.add_account_popup)
        addDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val addBtn = findViewById<Button>(R.id.addBtn)

        addBtn.setOnClickListener { view ->
            addDialog.show()
        }

        //calls UI from popup xml
        val exitBtn = addDialog.findViewById<Button>(R.id.exitBtn)
        val positionSpinner: Spinner = addDialog.findViewById(R.id.positionSpinner)
        val createAccBtn = addDialog.findViewById<Button>(R.id.createAccBtn)
        val usernameTxtInput = addDialog.findViewById<TextInputLayout>(R.id.usernameTxtInput)
        val emailTxtInput = addDialog.findViewById<TextInputLayout>(R.id.emailTxtInput)
        val passwordTxtInput = addDialog.findViewById<TextInputLayout>(R.id.passwordTxtInput)

        exitBtn.setOnClickListener { view ->
            addDialog.hide()
        }

        //convert enum class to list of display
        val positionsList = Positions.values().map { it.displayName }

        //adapter for position spinner
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            positionsList
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = spinnerAdapter

        createAccBtn.setOnClickListener{
            //create account object
            val account = Accounts(
                username = usernameTxtInput.editText?.text.toString(),
                email = emailTxtInput.editText?.text.toString(),
                password = passwordTxtInput.editText?.text.toString(),
                position = positionSpinner.selectedItem.toString()
            )

            //insert into database
            viewModel.addAccount(account)
            addDialog.dismiss()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.accountList)

        val infoDialog = Dialog(this)
        infoDialog.setContentView(R.layout.item_information)
        infoDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val adapter = AccountsAdapter(emptyList()) { clickedAccount ->

            // Get dialog views
            val infoUsername = infoDialog.findViewById<TextView>(R.id.txtNameInfo)
            val infoEmail = infoDialog.findViewById<TextView>(R.id.txtEmailInfo)
            val infoPassword = infoDialog.findViewById<TextView>(R.id.txtPasswordInfo)
            val infoPosition = infoDialog.findViewById<TextView>(R.id.txtPositionInfo)

            // Fill dialog with clicked user info
            infoUsername.text = clickedAccount.username
            infoEmail.text = clickedAccount.email
            infoPassword.text = clickedAccount.password
            infoPosition.text = clickedAccount.position

            infoDialog.show()
        }

        recyclerView.addItemDecoration(SpacingItemDecoration(20)) //Gap between items
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //observe LiveData from ViewModel
        viewModel.readAllData.observe(this) { accounts ->
            accounts?.let {
                adapter.setData(it)
            }
        }


    }
}