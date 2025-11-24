package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAccount(accounts : Accounts)

    @Query("SELECT * FROM accounts ORDER BY userId ASC")
    fun readAllData(): LiveData<List<Accounts>>         //Lists all accounts (for Admin)

}