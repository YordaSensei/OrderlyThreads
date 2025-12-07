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

@Database(entities = [Accounts::class, Orders::class, Inventory::class, OrderCheck::class], version = 7, exportSchema = false)
abstract class OrderlyThreadsDatabase : RoomDatabase() {

    abstract fun accountsDao(): AccountsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun orderCheckDao(): OrderCheckDao

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
                database.execSQL("ALTER TABLE orders ADD COLUMN status TEXT NOT NULL DEFAULT 'Pending Approval'")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `order_checks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `orderId` INTEGER NOT NULL, `inventoryId` INTEGER NOT NULL, `status` TEXT NOT NULL, FOREIGN KEY(`orderId`) REFERENCES `orders`(`orderId`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`inventoryId`) REFERENCES `inventory`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)"
                )
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

                        db.execSQL("INSERT INTO Accounts (username, email, password, position) " +
                                "VALUES ('admin', 'admin@gmail.com', 'admin123', 'Admin')")

                        CoroutineScope(Dispatchers.IO).launch {
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
                
                .addMigrations(MIGRATION_1_2, MIGRATION_5_6, MIGRATION_6_7)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}