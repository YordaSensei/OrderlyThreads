package com.example.orderlythreads.Database

class OrdersRepository(private val ordersDao: OrdersDao) {

    suspend fun insertOrder(order: Orders) {
        ordersDao.insertOrder(order)
    }

    suspend fun getAllOrders(): List<Orders> {
        return ordersDao.getAllOrders()
    }
}
