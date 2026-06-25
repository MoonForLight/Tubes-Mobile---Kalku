package com.example.kalku.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val indonesiaLocale = Locale("id", "ID")

    fun formatRupiah(value: Long): String {
        return NumberFormat.getCurrencyInstance(indonesiaLocale).apply {
            maximumFractionDigits = 0
            minimumFractionDigits = 0
        }.format(value)
    }

    /** Menghapus Rp, titik, spasi, dan karakter selain angka. */
    fun parseCurrency(text: String): Long {
        return text.filter { it.isDigit() }.toLongOrNull() ?: 0L
    }
}
