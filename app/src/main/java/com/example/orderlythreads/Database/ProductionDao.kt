package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update@Dao
interface ProductionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(production: Production)

    @Update
    suspend fun update(production: Production)

    // In ProductionDao.kt inside the interface
    @Query("UPDATE production SET prodStatus = :status WHERE productionId = :id")
    suspend fun updateStatus(id: Int, status: ProductionStatus)

    // This query joins the Production table with the Order table
    // so we can display the Client Name alongside the Production Status.
    @Query("""
        SELECT p.productionId, p.orderId, p.prodStatus AS status, o.dueDate, o.clientName 
        FROM production p
        INNER JOIN orders o ON p.orderId = o.orderId
        WHERE prodStatus != 'FINISHING'
    """)
    fun getAllActiveProduction(): LiveData<List<ProductionWithDetails>>

    @Query("SELECT * FROM production WHERE productionId = :id")
    suspend fun getProductionById(id: Int): Production?
}