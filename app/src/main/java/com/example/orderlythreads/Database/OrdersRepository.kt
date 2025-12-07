package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData

class OrdersRepository(private val ordersDao: OrdersDao) {

    suspend fun insertOrder(order: Orders) {
        ordersDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Orders) {
        ordersDao.updateOrder(order)
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        ordersDao.updateOrderStatus(orderId, status)
    }

    fun getAllOrders(): LiveData<List<Orders>> {
        return ordersDao.getAllOrders()
    }

    suspend fun getOrderById(orderId: Int): Orders? {
        return ordersDao.getOrderById(orderId)
    }
    // In OrdersRepository class
    val readApprovedOrders: LiveData<List<Orders>> = ordersDao.getApprovedOrders()
}