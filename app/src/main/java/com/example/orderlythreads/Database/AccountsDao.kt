package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)     //If registered user already exists = IGNORE
    suspend fun addAccount(accounts : Accounts)

    @Query("SELECT * FROM accounts ORDER BY userId ASC")
    fun readAllData(): LiveData<List<Accounts>>         //Lists all accounts (for Admin)

    @Query("DELETE FROM accounts WHERE userId = :userId")
    suspend fun deleteById(userId: Int)

    @Update
    suspend fun updateAccount(accounts : Accounts)

    @Query("SELECT * FROM accounts WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Accounts?
}