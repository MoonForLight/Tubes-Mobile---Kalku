package com.example.kalku.calculator

import kotlin.math.roundToLong

/**
 * Rumus inti penentuan harga jual Kalku.
 */
object CalculationHelper {

    fun calculate(
        productName: String,
        productionCost: Long,
        operationalCost: Long,
        quantity: Int,
        profitPercentage: Double
    ): CalculationResultData {
        require(productName.isNotBlank()) { "Nama produk harus diisi" }
        require(productionCost >= 0) { "Biaya produksi tidak boleh negatif" }
        require(operationalCost >= 0) { "Biaya operasional tidak boleh negatif" }
        require(quantity > 0) { "Jumlah produk harus lebih dari 0" }
        require(profitPercentage >= 0) { "Persentase keuntungan tidak boleh negatif" }

        val totalCost = productionCost + operationalCost
        val costPerItem = (totalCost.toDouble() / quantity).roundToLong()
        val profitPerItem = (costPerItem * profitPercentage / 100.0).roundToLong()
        val sellingPrice = costPerItem + profitPerItem
        val totalProfit = profitPerItem * quantity

        return CalculationResultData(
            productName = productName,
            productionCost = productionCost,
            operationalCost = operationalCost,
            quantity = quantity,
            profitPercentage = profitPercentage,
            totalCost = totalCost,
            costPerItem = costPerItem,
            profitPerItem = profitPerItem,
            sellingPrice = sellingPrice,
            totalProfit = totalProfit
        )
    }
}
