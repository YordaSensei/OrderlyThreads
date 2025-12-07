package com.example.orderlythreads.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.net.Uri 

@Database(entities = [Accounts::class, Orders::class, Inventory::class], version = 6, exportSchema = false)
abstract class OrderlyThreadsDatabase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun inventoryDao(): InventoryDao

    companion object {
        @Volatile
        private var INSTANCE: OrderlyThreadsDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE inventory_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, category TEXT NOT NULL, material TEXT NOT NULL, quantity INTEGER NOT NULL, imageUri TEXT)"
                )
                database.execSQL(
                    "INSERT INTO inventory_new (id, category, material, quantity) SELECT id, category, material, quantity FROM inventory"
                )
                database.execSQL("DROP TABLE inventory")
                database.execSQL("ALTER TABLE inventory_new RENAME TO inventory")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add 'status' column to 'orders' table with a default value
                database.execSQL("ALTER TABLE orders ADD COLUMN status TEXT NOT NULL DEFAULT 'Pending Approval'")
            }
        }

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

                        // Insert admin and default inventory
                        CoroutineScope(Dispatchers.IO).launch {
                            // Pre-populate Fabric inventory items
                            val inventoryDao = INSTANCE?.inventoryDao()
                            val packageName = context.packageName
                            val fabrics = listOf(
                                "img_fabric_merino", "img_fabric_lambswool", "img_fabric_cashmere",
                                "img_fabric_milano_ribbed", "img_fabric_scouffle_yarn", "img_fabric_alpaca"
                            )
                            
                            fabrics.forEach { fabricResourceName ->
                                val materialName = fabricResourceName
                                    .removePrefix("img_fabric_")
                                    .replace("_", " ")
                                    .split(" ")
                                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                                
                                val imageUri = Uri.parse("android.resource://$packageName/drawable/$fabricResourceName").toString()
                                val inventory = Inventory(
                                    category = "Fabric",
                                    material = materialName,
                                    quantity = 40,
                                    imageUri = imageUri
                                )
                                inventoryDao?.addItem(inventory)
                            }
                        }
                    }
                })
                .addMigrations(MIGRATION_1_2, MIGRATION_5_6)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}