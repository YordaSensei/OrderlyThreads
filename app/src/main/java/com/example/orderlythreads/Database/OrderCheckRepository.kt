package com.example.orderlythreads.Database

class OrderCheckRepository(private val orderCheckDao: OrderCheckDao) {
    suspend fun insertOrderCheck(orderCheck: OrderCheck) {
        orderCheckDao.insertOrderCheck(orderCheck)
    }

    // In OrderCheckRepository.kt
    suspend fun updateStatus(id: Int, status: String) {
        orderCheckDao.updateStatus(id, status)
    }
}
