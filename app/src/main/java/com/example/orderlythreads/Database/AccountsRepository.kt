package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData

class AccountsRepository(private val accountsDao: AccountsDao) {

    suspend fun login(email: String, password: String): Accounts? {
        return accountsDao.login(email, password)
    }
    val readAllData: LiveData<List<Accounts>> = accountsDao.readAllData()

    suspend fun addAccount(accounts: Accounts){
        accountsDao.addAccount(accounts)
    }

    suspend fun deleteById(id: Int) {
        accountsDao.deleteById(id)
    }

    suspend fun updateAccount(accounts: Accounts) {
        accountsDao.updateAccount(accounts)
    }
}