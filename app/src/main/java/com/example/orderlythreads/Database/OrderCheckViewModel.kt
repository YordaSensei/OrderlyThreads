package com.example.orderlythreads.Database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrderCheckViewModel(private val repository: OrderCheckRepository) : ViewModel() {
    fun addOrderCheck(orderCheck: OrderCheck) {
        viewModelScope.launch {
            repository.insertOrderCheck(orderCheck)
        }
    }
}
