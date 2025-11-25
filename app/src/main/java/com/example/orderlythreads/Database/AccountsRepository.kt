package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData

class AccountsRepository(private val accountsDao: AccountsDao) {

    val readAllData: LiveData<List<Accounts>> = accountsDao.readAllData()

    suspend fun addAccount(accounts: Accounts){
        accountsDao.addAccount(accounts)
    }

}