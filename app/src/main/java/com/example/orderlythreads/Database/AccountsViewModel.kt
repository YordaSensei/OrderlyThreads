package com.example.orderlythreads.Database

import android.app.Application
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountsViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Accounts>>
    private val repository: AccountsRepository

    init {
        val accountsDao = OrderlyThreadsDatabase.getDatabase(application).accountsDao()
        repository = AccountsRepository(accountsDao)
        readAllData = repository.readAllData
    }

    fun login(email: String, password: String, callback: (Accounts?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val account = repository.login(email, password)

            withContext(Dispatchers.Main) {
                callback(account)
            }
        }
    }

    fun addAccount(accounts: Accounts){
        viewModelScope.launch (Dispatchers.IO) {
            repository.addAccount(accounts)
        }
    }

    fun deleteById(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteById(id)
        }
    }

    fun updateAccount(accounts: Accounts){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAccount(accounts)
        }
    }
}