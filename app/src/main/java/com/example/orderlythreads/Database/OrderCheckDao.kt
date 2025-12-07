package com.example.orderlythreads.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrderCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderCheck(orderCheck: OrderCheck)

    // In OrderCheckDao.kt
    @Query("UPDATE order_checks SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Int, status: String)
}
