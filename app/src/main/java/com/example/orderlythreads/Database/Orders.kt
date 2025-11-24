package com.example.orderlythreads.Database


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "orders")
data class Orders (
    @PrimaryKey(autoGenerate = true)
    val orderId: Int = 0,

    val clientName: String,
    val contact: Int,
    //val orderDate: Date, //should be automatic based on time of order creation
    val dueDate: Date,
    val quantity: Int,
    val chest: Int,
    val shoulderWidth: Int,
    val sleeveLength: Int,
    val armhole: Int,
    val waist: Int,
    val neckline: Int,
    val garmentLength: Int,
    val additionalNotes: String
    )