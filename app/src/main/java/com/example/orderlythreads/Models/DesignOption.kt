package com.example.orderlythreads.Models

data class DesignOption(
    val name: String,
    val imageResId: Int, // We will use R.drawable.something here
    var isSelected: Boolean = false
)
