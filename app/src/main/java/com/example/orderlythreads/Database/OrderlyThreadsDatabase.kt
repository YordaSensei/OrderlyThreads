package com.example.orderlythreads.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Accounts::class], version = 1, exportSchema = false)
abstract class OrderlyThreadsDatabase: RoomDatabase() {

    abstract fun accountsDao(): AccountsDao

    companion object {
        @Volatile
        private var INSTANCE: OrderlyThreadsDatabase? = null

        fun getDatabase(context: Context): OrderlyThreadsDatabase{
            val tempInstance = INSTANCE     //Temporary Instance

            //If a temporary instance already exists = RETURN
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrderlyThreadsDatabase::class.java,
                    "orderlyThreads_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }

}