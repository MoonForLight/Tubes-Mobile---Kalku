package com.example.kalku.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CalculationHelperTest {

    @Test
    fun calculate_standardCase_returnsExpectedValues() {
        val result = CalculationHelper.calculate(
            productName = "Keripik",
            productionCost = 75_000,
            operationalCost = 25_000,
            quantity = 10,
            profitPercentage = 25.0
        )

        assertEquals(100_000, result.totalCost)
        assertEquals(10_000, result.costPerItem)
        assertEquals(2_500, result.profitPerItem)
        assertEquals(12_500, result.sellingPrice)
        assertEquals(25_000, result.totalProfit)
    }

    @Test
    fun calculate_nonDivisibleCost_roundsUpAndCoversCapital() {
        val result = CalculationHelper.calculate(
            productName = "Produk",
            productionCost = 10_000,
            operationalCost = 1,
            quantity = 3,
            profitPercentage = 20.0
        )

        assertEquals(3_334, result.costPerItem)
        assertEquals(4_001, result.sellingPrice)
        assertEquals(2_002, result.totalProfit)
    }

    @Test
    fun calculate_profitAbove100_throwsError() {
        assertThrows(IllegalArgumentException::class.java) {
            CalculationHelper.calculate("Produk", 10_000, 0, 1, 101.0)
        }
    }
}
