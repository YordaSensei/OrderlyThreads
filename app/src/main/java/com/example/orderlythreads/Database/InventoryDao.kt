package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InventoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addItem(inventory: Inventory)

    @Update
    suspend fun updateItem(inventory: Inventory)

    @Delete
    suspend fun deleteItem(inventory: Inventory)

    @Query("SELECT * FROM inventory WHERE category = :category ORDER BY id ASC")
    fun getInventoryByCategory(category: String): LiveData<List<Inventory>>

    @Query("SELECT * FROM inventory WHERE category = :category AND material LIKE :searchQuery ORDER BY id ASC")
    fun searchInventoryByCategory(category: String, searchQuery: String): LiveData<List<Inventory>>
}