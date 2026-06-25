package com.example.kalku.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.ActivityCalculationResultBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import com.example.kalku.MainActivity
import kotlinx.coroutines.launch

class CalculationResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculationResultBinding
    private lateinit var result: CalculationResultData
    private var isSaved = false
    private var productId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculationResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getIntExtra(CalculatorActivity.EXTRA_PRODUCT_ID, 0).takeIf { it > 0 }
        isSaved = savedInstanceState?.getBoolean(KEY_IS_SAVED, false) ?: false
        result = readResultFromIntent()
        showResult()
        if (isSaved) {
            binding.btnSaveResult.text = "Tersimpan"
            binding.btnSaveResult.isEnabled = false
        }

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRecalculate.setOnClickListener { finish() }
        binding.btnSaveResult.setOnClickListener { saveResult() }
        binding.btnShareResult.setOnClickListener { shareResult() }
    }

    private fun readResultFromIntent(): CalculationResultData {
        return CalculationResultData(
            productName = intent.getStringExtra(CalculatorActivity.EXTRA_PRODUCT_NAME).orEmpty(),
            productionCost = intent.getLongExtra(CalculatorActivity.EXTRA_PRODUCTION_COST, 0L),
            operationalCost = intent.getLongExtra(CalculatorActivity.EXTRA_OPERATIONAL_COST, 0L),
            quantity = intent.getIntExtra(CalculatorActivity.EXTRA_QUANTITY, 1),
            profitPercentage = intent.getDoubleExtra(CalculatorActivity.EXTRA_PROFIT_PERCENTAGE, 0.0),
            totalCost = intent.getLongExtra(CalculatorActivity.EXTRA_TOTAL_COST, 0L),
            costPerItem = intent.getLongExtra(CalculatorActivity.EXTRA_COST_PER_ITEM, 0L),
            profitPerItem = intent.getLongExtra(CalculatorActivity.EXTRA_PROFIT_PER_ITEM, 0L),
            sellingPrice = intent.getLongExtra(CalculatorActivity.EXTRA_SELLING_PRICE, 0L),
            totalProfit = intent.getLongExtra(CalculatorActivity.EXTRA_TOTAL_PROFIT, 0L)
        )
    }

    private fun showResult() = with(binding) {
        tvProductName.text = result.productName
        tvSellingPrice.text = CurrencyUtils.formatRupiah(result.sellingPrice)
        tvTotalCost.text = CurrencyUtils.formatRupiah(result.totalCost)
        tvTotalProfit.text = CurrencyUtils.formatRupiah(result.totalProfit)
        tvProductionCost.text = CurrencyUtils.formatRupiah(result.productionCost)
        tvOperationalCost.text = CurrencyUtils.formatRupiah(result.operationalCost)
        tvCostPerItem.text = CurrencyUtils.formatRupiah(result.costPerItem)
        tvProfitPerItem.text = CurrencyUtils.formatRupiah(result.profitPerItem)
        tvQuantity.text = "${result.quantity} produk"
        tvMargin.text = "${result.profitPercentage.toInt()}%"
        profitDonut.setPercentage(result.profitPercentage)
    }

    private fun saveResult() {
        if (isSaved) {
            Toast.makeText(this, "Hasil ini sudah disimpan", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionManager = SessionManager(this)
        val calculation = CalculationEntity(
            userId = sessionManager.getUserId(),
            productId = productId,
            productName = result.productName,
            productionCost = result.productionCost,
            operationalCost = result.operationalCost,
            quantity = result.quantity,
            profitPercentage = result.profitPercentage,
            totalCost = result.totalCost,
            costPerItem = result.costPerItem,
            profitPerItem = result.profitPerItem,
            sellingPrice = result.sellingPrice,
            totalProfit = result.totalProfit
        )

        lifecycleScope.launch {
            AppDatabase.getDatabase(this@CalculationResultActivity)
                .calculationDao()
                .insert(calculation)

            isSaved = true
            binding.btnSaveResult.text = "Tersimpan"
            binding.btnSaveResult.isEnabled = false
            Toast.makeText(
                this@CalculationResultActivity,
                "Hasil perhitungan berhasil disimpan",
                Toast.LENGTH_SHORT
            ).show()
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(KEY_IS_SAVED, isSaved)
        super.onSaveInstanceState(outState)
    }

    private fun shareResult() {
        val message = buildString {
            appendLine("Hasil Perhitungan Kalku")
            appendLine("Produk: ${result.productName}")
            appendLine("Harga jual per produk: ${CurrencyUtils.formatRupiah(result.sellingPrice)}")
            appendLine("Modal per produk: ${CurrencyUtils.formatRupiah(result.costPerItem)}")
            appendLine("Keuntungan per produk: ${CurrencyUtils.formatRupiah(result.profitPerItem)}")
            appendLine("Estimasi total keuntungan: ${CurrencyUtils.formatRupiah(result.totalProfit)}")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan hasil melalui"))
    }

    companion object {
        private const val KEY_IS_SAVED = "is_saved"
    }
}
