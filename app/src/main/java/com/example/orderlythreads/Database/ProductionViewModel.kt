package com.example.orderlythreads.Database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductionRepository

    // This is what the ProductionStaff activity observes
    val allProductionJobs: LiveData<List<ProductionWithDetails>>

    init {
        // Get reference to the DB and DAO
        val productionDao = OrderlyThreadsDatabase.getDatabase(application).productionDao()
        repository = ProductionRepository(productionDao)
        allProductionJobs = repository.allActiveProduction
    }

    fun insert(production: Production) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(production)
        }
    }

    fun updateStatus(production: Production) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(production)
        }
    }

    fun updateStatusById(id: Int, status: ProductionStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateStatus(id, status)
        }
    }
}
