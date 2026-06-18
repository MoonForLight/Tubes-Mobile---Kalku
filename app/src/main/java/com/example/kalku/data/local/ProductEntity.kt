package com.example.kalku.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "products",
    indices = [Index("userId"), Index("category")]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val productName: String,
    val category: String,
    val productionCost: Long,
    val operationalCost: Long,
    val quantity: Int,
    val profitPercentage: Double,
    val sellingPrice: Long,
    val totalProfit: Long,
    @ColumnInfo(defaultValue = "''")
    val imageUri: String = "",
    @ColumnInfo(defaultValue = "1")
    val isActive: Boolean = true,
    @ColumnInfo(defaultValue = "5")
    val lowStockThreshold: Int = 5,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun stockStatus(): ProductStockStatus = when {
        !isActive -> ProductStockStatus.INACTIVE
        quantity <= 0 -> ProductStockStatus.OUT_OF_STOCK
        quantity <= lowStockThreshold -> ProductStockStatus.LOW_STOCK
        else -> ProductStockStatus.ACTIVE
    }
}

enum class ProductStockStatus {
    ACTIVE,
    LOW_STOCK,
    OUT_OF_STOCK,
    INACTIVE
}
