package com.example.orderlythreads.Database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Correct: Pass BOTH the repository and the productionDao in the constructor
class OrderCheckViewModel(
    private val checkRepository: OrderCheckRepository,
    private val productionDao: ProductionDao
) : ViewModel() {

    // No init block needed. We get what we need from the constructor.

    fun addOrderCheck(orderCheck: OrderCheck) {
        viewModelScope.launch {
            checkRepository.insertOrderCheck(orderCheck)
        }
    }

    fun approveOrder(checkId: Int, orderId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Update OrderCheck status to 'Approved'
            checkRepository.updateStatus(checkId, "Approved")

            // 2. INSERT INTO PRODUCTION TABLE
            val newJob = Production(
                orderId = orderId,
                prodStatus = ProductionStatus.PENDING
            )
            productionDao.insert(newJob)
        }
    }
}
