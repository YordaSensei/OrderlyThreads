package com.example.orderlythreads.Database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class OrdersViewModelFactory(private val repository: OrdersRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrdersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrdersViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
