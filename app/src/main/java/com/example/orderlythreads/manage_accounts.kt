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



class manage_accounts : AppCompatActivity() {
    private lateinit var viewModel: AccountsViewModel
    private var selectedAccountId: Int? = null
    private var selectedAccount: Accounts? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[AccountsViewModel::class.java]
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

            selectedAccount = clickedAccount
            selectedAccountId = clickedAccount.userId
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

        val updateDialog = Dialog(this)
        updateDialog.setContentView(R.layout.update_account_popup)
        updateDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val newUsernameInput = updateDialog.findViewById<TextInputLayout>(R.id.usernameTxtInput)
        val newEmailInput = updateDialog.findViewById<TextInputLayout>(R.id.emailTxtInput)
        val newPasswordInput = updateDialog.findViewById<TextInputLayout>(R.id.passwordTxtInput)
        val newPositionSpinner = updateDialog.findViewById<Spinner>(R.id.positionSpinner)
        val updateAccBtn = updateDialog.findViewById<Button>(R.id.updateAccBtn)

        val updateBtn = infoDialog.findViewById<Button>(R.id.updateAccBtn)

        updateBtn.setOnClickListener {

            val acc = selectedAccount ?: return@setOnClickListener

            // Prefill
            newUsernameInput.editText?.setText(acc.username)
            newEmailInput.editText?.setText(acc.email)
            newPasswordInput.editText?.setText(acc.password)

            // Spinner setup
            val updatePositionsList = Positions.values().map { it.displayName }
            val updateSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, updatePositionsList)
            updateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            newPositionSpinner.adapter = updateSpinnerAdapter

            // Select current position
            val posIndex = updatePositionsList.indexOf(acc.position)
            if (posIndex >= 0) newPositionSpinner.setSelection(posIndex)

            infoDialog.dismiss()
            updateDialog.show()
        }

        updateAccBtn.setOnClickListener {

            val acc = selectedAccount ?: return@setOnClickListener

            val updatedAccount = Accounts(
                userId = acc.userId,
                username = newUsernameInput.editText?.text.toString(),
                email = newEmailInput.editText?.text.toString(),
                password = newPasswordInput.editText?.text.toString(),
                position = newPositionSpinner.selectedItem.toString()
            )

            viewModel.updateAccount(updatedAccount)
            updateDialog.dismiss()
        }


        val deleteBtn = infoDialog.findViewById<Button>(R.id.deleteAccBtn)

        deleteBtn.setOnClickListener {
            selectedAccountId?.let{ id ->
                viewModel.deleteById(id)
            }
            infoDialog.dismiss()
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