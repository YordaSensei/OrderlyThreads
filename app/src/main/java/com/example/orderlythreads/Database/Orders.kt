package com.example.orderlythreads.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
// You can remove: import java.util.Date

@Entity(tableName = "orders")
data class Orders (
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,

    val clientName: String,
    val contact: String,

    val orderDate: String, // Automatic (Created Date)
    val dueDate: String,   // Selected by User

    val quantity: Int,
    // Changed measurements to String to prevent crashes on empty inputs,
    // OR keep as Int/Double if you ensure they are never empty.
    // Assuming Int for now based on your file:
    val chest: String,
    val shoulderWidth: String,
    val sleeveLength: String,
    val armhole: String,
    val waist: String,
    val neckline: String,
    val garmentLength: String,
    val additionalNotes: String
)
