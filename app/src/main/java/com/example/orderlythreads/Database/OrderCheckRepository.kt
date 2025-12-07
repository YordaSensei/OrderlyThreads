package com.example.orderlythreads.Database

class OrderCheckRepository(private val orderCheckDao: OrderCheckDao) {
    suspend fun insertOrderCheck(orderCheck: OrderCheck) {
        orderCheckDao.insertOrderCheck(orderCheck)
    }
}
