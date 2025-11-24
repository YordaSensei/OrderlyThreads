package com.example.orderlythreads.Database


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "completed_orders",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Orders::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]

)
data class Production (
    @PrimaryKey(autoGenerate = true)
    val completedOrderId: Int = 0,

    val orderId: Int,    // FK to Orders.kt
    val completionDate: Date
)