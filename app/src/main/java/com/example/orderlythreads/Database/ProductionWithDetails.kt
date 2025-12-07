package com.example.orderlythreads.Database

import androidx.room.Embedded

data class ProductionWithDetails(
    val productionId: Int,
    val orderId: Int,
    val status: ProductionStatus,
    val dueDate: String,
    val clientName: String // This comes from the Orders table
)
