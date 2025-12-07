package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: OrdersRepository) : ViewModel() {

    val allOrders: LiveData<List<Orders>> = repository.getAllOrders()

    fun addOrder(order: Orders) {
        viewModelScope.launch {
            repository.insertOrder(order)
        }
    }

    fun updateOrder(order: Orders) {
        viewModelScope.launch {
            repository.updateOrder(order)
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }
}