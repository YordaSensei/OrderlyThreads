package com.example.orderlythreads.Database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Accounts>>
    private val repository: AccountsRepository

    init {
        val accountsDao = OrderlyThreadsDatabase.getDatabase(application).accountsDao()
        repository = AccountsRepository(accountsDao)
        readAllData = repository.readAllData
    }

    fun addAccount(accounts: Accounts){
        viewModelScope.launch (Dispatchers.IO) {
            repository.addAccount(accounts)
        }
    }
}