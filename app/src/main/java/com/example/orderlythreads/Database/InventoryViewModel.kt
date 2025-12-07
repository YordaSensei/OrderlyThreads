package com.example.orderlythreads.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val inventoryItems: LiveData<List<Inventory>> = _selectedCategory.switchMap { category ->
        _searchQuery.switchMap { query ->
            if (query.isEmpty()) {
                repository.getInventoryByCategory(category)
            } else {
                repository.searchInventoryByCategory(category, "%$query%")
            }
        }
    }

    init {

        _selectedCategory.value = "Fabric"
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
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

    // Helper to get only "Fabric" items
    fun getFabrics(): LiveData<List<Inventory>> {
        return repository.getInventoryByCategory("Fabric")
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