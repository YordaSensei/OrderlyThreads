package com.example.orderlythreads.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.lifecycle.LiveData

@Dao
interface OrdersDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: Orders)

    @Update
    suspend fun updateOrder(order: Orders)

    @Query("UPDATE orders SET status = :status WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): LiveData<List<Orders>>
}