package com.example.orderlythreads.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Orders (
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,

    val clientName: String,
    val contact: String,
    val orderDate: String, // Automatic (Created Date)
    val dueDate: String,   // Selected by User
    val quantity: Int,

    val waist: String,
    val hips: String,
    val chest: String,
    val length: String,
    val shoulder: String,
    val sleeve: String,

    val upperDesignId: Int,       // Store the Drawable ID (e.g., R.drawable.tshirt)
    val upperFabricId: Int,       // Store the Drawable ID
    val upperColorHex: String,    // Store the Color Hex Code (e.g., "#FF0000")
    val upperAccentDesignId: Int,
    val upperAccentColorHex: String,
    val upperAccentQuantity: Int,

    // Lower Wear Details
    val lowerDesignId: Int,
    val lowerFabricId: Int,
    val lowerColorHex: String,
    val lowerAccentDesignId: Int,
    val lowerAccentColorHex: String,
    val lowerAccentQuantity: Int,

    val additionalNotes: String,
    
    // New field for Stock Check
    val status: String = "Pending Approval"
)