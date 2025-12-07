package com.example.orderlythreads.Database

import androidx.lifecycle.ViewModel // Change from AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: OrdersRepository) : ViewModel() {

    fun addOrder(order: Orders) {
        viewModelScope.launch {
            repository.insertOrder(order)
        }
    }
}
