package com.example.orderlythreads.Database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OrdersViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrdersRepository

    init {
        // Initialize Database and Repository
        val database = OrderlyThreadsDatabase.getDatabase(application)
        val ordersDao = database.ordersDao()
        repository = OrdersRepository(ordersDao)
    }

    fun addOrder(order: Orders) {
        viewModelScope.launch {
            repository.insertOrder(order)
        }
    }
}
