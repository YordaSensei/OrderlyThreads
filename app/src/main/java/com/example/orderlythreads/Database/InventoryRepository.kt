package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData

class InventoryRepository(private val inventoryDao: InventoryDao) {

    fun getInventoryByCategory(category: String): LiveData<List<Inventory>> {
        return inventoryDao.getInventoryByCategory(category)
    }

    suspend fun addItem(inventory: Inventory) {
        inventoryDao.addItem(inventory)
    }

    suspend fun updateItem(inventory: Inventory) {
        inventoryDao.updateItem(inventory)
    }

    suspend fun deleteItem(inventory: Inventory) {
        inventoryDao.deleteItem(inventory)
    }
}