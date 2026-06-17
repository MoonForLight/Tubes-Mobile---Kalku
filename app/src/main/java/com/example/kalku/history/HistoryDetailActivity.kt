package com.example.kalku.history

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.calculator.CalculatorActivity
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.ActivityHistoryDetailBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.DateUtils
import kotlinx.coroutines.launch

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private var calculation: CalculationEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnDelete.setOnClickListener { confirmDelete() }
        binding.btnRecalculate.setOnClickListener { recalculate() }
        loadData()
    }

    private fun loadData() {
        val id = intent.getIntExtra(EXTRA_CALCULATION_ID, 0)
        lifecycleScope.launch {
            calculation = AppDatabase.getDatabase(this@HistoryDetailActivity)
                .calculationDao()
                .getCalculationById(id)

            val item = calculation
            if (item == null) {
                Toast.makeText(this@HistoryDetailActivity, "Riwayat tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            binding.tvProductName.text = item.productName
            binding.tvDate.text = DateUtils.formatDate(item.createdAt)
            binding.tvSellingPrice.text = CurrencyUtils.formatRupiah(item.sellingPrice)
            binding.tvTotalCost.text = CurrencyUtils.formatRupiah(item.totalCost)
            binding.tvTotalProfit.text = CurrencyUtils.formatRupiah(item.totalProfit)
            binding.tvProductionCost.text = "Biaya produksi: ${CurrencyUtils.formatRupiah(item.productionCost)}"
            binding.tvOperationalCost.text = "Biaya operasional: ${CurrencyUtils.formatRupiah(item.operationalCost)}"
            binding.tvQuantity.text = "Jumlah produk: ${item.quantity}"
            binding.tvProfitPercentage.text = "Margin keuntungan: ${item.profitPercentage.toInt()}%"
        }
    }

    private fun recalculate() {
        val item = calculation ?: return
        startActivity(Intent(this, CalculatorActivity::class.java).apply {
            putExtra(CalculatorActivity.EXTRA_PRODUCT_NAME, item.productName)
            putExtra(CalculatorActivity.EXTRA_PRODUCTION_COST, item.productionCost)
            putExtra(CalculatorActivity.EXTRA_OPERATIONAL_COST, item.operationalCost)
            putExtra(CalculatorActivity.EXTRA_QUANTITY, item.quantity)
            putExtra(CalculatorActivity.EXTRA_PROFIT_PERCENTAGE, item.profitPercentage)
        })
    }

    private fun confirmDelete() {
        val item = calculation ?: return
        AlertDialog.Builder(this)
            .setTitle("Hapus riwayat")
            .setMessage("Hapus riwayat ${item.productName}?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                lifecycleScope.launch {
                    AppDatabase.getDatabase(this@HistoryDetailActivity).calculationDao().delete(item)
                    Toast.makeText(this@HistoryDetailActivity, "Riwayat berhasil dihapus", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .show()
    }

    companion object {
        const val EXTRA_CALCULATION_ID = "calculation_id"
    }
}
