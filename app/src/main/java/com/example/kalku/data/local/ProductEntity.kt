package com.example.kalku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
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
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
