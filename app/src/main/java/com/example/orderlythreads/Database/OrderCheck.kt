package com.example.orderlythreads.Database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_checks",
    foreignKeys = [
        ForeignKey(
            entity = Orders::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Inventory::class,
            parentColumns = ["id"],
            childColumns = ["inventoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderCheck(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val orderId: Int,
    val inventoryId: Int,
    val status: String // "Approved" or "Rejected"
)
