package com.example.orderlythreads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.orderlythreads.Database.Inventory
import com.example.orderlythreads.Database.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    val inventoryItems: LiveData<List<Inventory>> = _selectedCategory.switchMap {
        repository.getInventoryByCategory(it)
    }

    init {
        // Set initial category, e.g., "Fabric"
        _selectedCategory.value = "Fabric"
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addItem(inventory: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        repository.addItem(inventory)
    }

    fun updateItem(inventory: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateItem(inventory)
    }

    fun deleteItem(inventory: Inventory) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(inventory)
    }
}

class InventoryViewModelFactory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}