package com.example.orderlythreads.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Accounts::class, Orders::class, Inventory::class], version = 4, exportSchema = false)
abstract class OrderlyThreadsDatabase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: OrderlyThreadsDatabase? = null

        fun getDatabase(context: Context): OrderlyThreadsDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderlyThreadsDatabase::class.java,
                    "orderlyThreads_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Insert admin safely
                            db.execSQL("INSERT INTO Accounts (username, email, password, position) " +
                                    "VALUES ('admin', 'admin@gmail.com', 'admin123', 'Admin')")
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}