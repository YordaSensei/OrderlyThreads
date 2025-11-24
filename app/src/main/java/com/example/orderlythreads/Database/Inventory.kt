package com.example.orderlythreads.Database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory")
data class Inventory (
    @PrimaryKey(autoGenerate = true)
    val inventoryId: Int = 0,

    val category: String,
    val material: String,
    val quantity: Int
)