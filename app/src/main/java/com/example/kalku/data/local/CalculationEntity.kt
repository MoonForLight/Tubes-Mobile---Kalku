package com.example.kalku.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "calculations",
    indices = [Index("userId"), Index("productId"), Index("createdAt")]
)
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val productId: Int? = null,
    val productName: String,
    val productionCost: Long,
    val operationalCost: Long,
    val quantity: Int,
    val profitPercentage: Double,
    val totalCost: Long,
    val costPerItem: Long,
    val profitPerItem: Long,
    val sellingPrice: Long,
    val totalProfit: Long,
    @ColumnInfo(defaultValue = "''")
    val imageUri: String = "",
    @ColumnInfo(defaultValue = "1")
    val isActive: Boolean = true,
    @ColumnInfo(defaultValue = "5")
    val lowStockThreshold: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)