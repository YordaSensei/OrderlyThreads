package com.example.orderlythreads

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.preference.contains
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.orderlythreads.Database.Inventory
import com.example.orderlythreads.Database.InventoryDao
import com.example.orderlythreads.Database.InventoryRepository
import com.example.orderlythreads.Database.OrderlyThreadsDatabase
import com.example.orderlythreads.Database.InventoryViewModel
import com.example.orderlythreads.Database.InventoryViewModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.example.orderlythreads.getOrAwaitValue // Assuming this is in LiveDataTestUtil.kt

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class InventoryRepositoryTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var inventoryDao: InventoryDao
    private lateinit var repository: InventoryRepository
    private lateinit var db: OrderlyThreadsDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, OrderlyThreadsDatabase::class.java)
            .allowMainThreadQueries() // For testing, avoid deadlock
            .build()
        inventoryDao = db.inventoryDao()
        repository = InventoryRepository(inventoryDao)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndGetItem() = runTest {
        val inventoryItem = Inventory(category = "Fabric", material = "Cotton", quantity = 10, imageUri = null) // Updated to use imageUri
        repository.addItem(inventoryItem)

        val allItems = repository.getInventoryByCategory("Fabric").getOrAwaitValue()
        assert(allItems.contains(inventoryItem.copy(id = allItems[0].id))) // Check if the item is in the list, accounting for auto-generated ID

        val retrievedItem = allItems.find { it.material == "Cotton" && it.category == "Fabric" }
        assert(retrievedItem != null)
        assert(retrievedItem?.quantity == 10)
    }
}
