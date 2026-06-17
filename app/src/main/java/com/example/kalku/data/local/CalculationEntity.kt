package com.example.kalku.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data hasil hitung yang nantinya dibaca oleh fitur Riwayat.
 */
@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
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
    val createdAt: Long = System.currentTimeMillis()
)
