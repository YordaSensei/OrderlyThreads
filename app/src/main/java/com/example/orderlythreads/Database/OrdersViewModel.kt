package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrdersViewModel(private val repository: OrdersRepository) : ViewModel() {

    val allOrders: LiveData<List<Orders>> = repository.getAllOrders()
    val readApprovedOrders: LiveData<List<Orders>>

    init {
        // ... existing init code ...
        readApprovedOrders = repository.readApprovedOrders
    }

    private val _currentOrder = MutableLiveData<Orders?>()
    val currentOrder: LiveData<Orders?> = _currentOrder

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

    fun fetchOrder(orderId: Int) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            _currentOrder.postValue(order)
        }
    }
}