package com.example.orderlythreads.Database


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "order_check",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Inventory::class,
            parentColumns = ["inventoryId"],
            childColumns = ["inventoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]

)
data class OrderCheck (
    @PrimaryKey(autoGenerate = true)
    val orderCheckId: Int = 0,

    val status: Boolean,
    val inventoryId: Int    // FK to Inventory.kt
)