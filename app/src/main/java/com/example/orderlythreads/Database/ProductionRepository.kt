package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData

class ProductionRepository(private val productionDao: ProductionDao) {

    // Helper variable to get the list from the DAO
    val allActiveProduction: LiveData<List<ProductionWithDetails>> = productionDao.getAllActiveProduction()

    suspend fun insert(production: Production) {
        productionDao.insert(production)
    }

    suspend fun update(production: Production) {
        productionDao.update(production)
    }

    suspend fun updateStatus(id: Int, status: ProductionStatus) {
        productionDao.updateStatus(id, status)
    }

    suspend fun getProductionById(id: Int): Production? {
        return productionDao.getProductionById(id)
    }
}
