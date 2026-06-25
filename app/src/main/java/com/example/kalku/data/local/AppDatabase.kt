package com.example.kalku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class, CalculationEntity::class, ProductEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun calculationDao(): CalculationDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS calculations (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        productName TEXT NOT NULL,
                        productionCost INTEGER NOT NULL,
                        operationalCost INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        profitPercentage REAL NOT NULL,
                        totalCost INTEGER NOT NULL,
                        costPerItem INTEGER NOT NULL,
                        profitPerItem INTEGER NOT NULL,
                        sellingPrice INTEGER NOT NULL,
                        totalProfit INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS products (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId INTEGER NOT NULL,
                        productName TEXT NOT NULL,
                        category TEXT NOT NULL,
                        productionCost INTEGER NOT NULL,
                        operationalCost INTEGER NOT NULL,
                        quantity INTEGER NOT NULL,
                        profitPercentage REAL NOT NULL,
                        sellingPrice INTEGER NOT NULL,
                        totalProfit INTEGER NOT NULL,
                        createdAt INTEGER NOT NULL,
                        updatedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN phone TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN address TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN businessDescription TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN photoUri TEXT NOT NULL DEFAULT ''")

                database.execSQL("ALTER TABLE products ADD COLUMN imageUri TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE products ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1")
                database.execSQL("ALTER TABLE products ADD COLUMN lowStockThreshold INTEGER NOT NULL DEFAULT 5")

                database.execSQL("ALTER TABLE calculations ADD COLUMN productId INTEGER")

                database.execSQL("CREATE INDEX IF NOT EXISTS index_users_email ON users(email)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_products_userId ON products(userId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_products_category ON products(category)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_calculations_userId ON calculations(userId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_calculations_productId ON calculations(productId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_calculations_createdAt ON calculations(createdAt)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kalku_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
