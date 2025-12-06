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

@Database(entities = [Accounts::class, Orders::class], version = 1, exportSchema = false)
abstract class OrderlyThreadsDatabase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun ordersDao(): OrdersDao

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
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.accountsDao()?.addAccount(
                                    Accounts(
                                        username = "admin",
                                        email = "admin@gmail.com",
                                        password = "admin123",
                                        position = "Admin"
                                    )
                                )
                            }
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