package com.example.orderlythreads.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Accounts (
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    val username: String,
    val email: String,
    val password: String,
    val position: String
    )