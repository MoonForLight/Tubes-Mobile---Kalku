package com.example.kalku.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculationHelperTest {

    @Test
    fun calculate_returnsExpectedSellingPrice() {
        val result = CalculationHelper.calculate(
            productName = "Produk Uji",
            productionCost = 75_000,
            operationalCost = 25_000,
            quantity = 10,
            profitPercentage = 25.0
        )

        assertEquals(100_000L, result.totalCost)
        assertEquals(10_000L, result.costPerItem)
        assertEquals(2_500L, result.profitPerItem)
        assertEquals(12_500L, result.sellingPrice)
        assertEquals(25_000L, result.totalProfit)
    }
}
