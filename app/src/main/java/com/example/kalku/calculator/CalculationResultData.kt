package com.example.kalku.calculator

/**
 * Menampung hasil perhitungan agar rumus tidak bercampur dengan kode tampilan.
 */
data class CalculationResultData(
    val productName: String,
    val productionCost: Long,
    val operationalCost: Long,
    val quantity: Int,
    val profitPercentage: Double,
    val totalCost: Long,
    val costPerItem: Long,
    val profitPerItem: Long,
    val sellingPrice: Long,
    val totalProfit: Long
)
