package com.example.kalku.calculator

import kotlin.math.ceil

/** Rumus inti penentuan harga jual Kalku. */
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
        require(profitPercentage in 0.0..100.0) { "Persentase keuntungan harus 0–100%" }

        val totalCost = Math.addExact(productionCost, operationalCost)
        val exactCostPerItem = totalCost.toDouble() / quantity.toDouble()
        // Dibulatkan ke atas agar seluruh modal tetap tertutup ketika biaya tidak habis dibagi.
        val costPerItem = ceil(exactCostPerItem).toLong()
        val exactSellingPrice = exactCostPerItem * (1.0 + profitPercentage / 100.0)
        val sellingPrice = ceil(exactSellingPrice).toLong().coerceAtLeast(costPerItem)
        val profitPerItem = (sellingPrice - costPerItem).coerceAtLeast(0L)
        val totalRevenue = sellingPrice * quantity
        val totalProfit = (totalRevenue - totalCost).coerceAtLeast(0L)

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
