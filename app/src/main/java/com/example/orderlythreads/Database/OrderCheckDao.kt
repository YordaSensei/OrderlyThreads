package com.example.orderlythreads.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface OrderCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderCheck(orderCheck: OrderCheck)
}
