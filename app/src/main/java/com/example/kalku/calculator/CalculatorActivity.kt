package com.example.kalku.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kalku.databinding.ActivityCalculatorBinding
import com.example.kalku.utils.CurrencyUtils

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantity()
            }
        }
        binding.btnPlus.setOnClickListener {
            if (quantity < 999999) {
                quantity++
                updateQuantity()
            }
        }

        binding.seekProfit.max = 1000
        binding.seekProfit.progress = 25
        updateProfitLabel(25)
        binding.seekProfit.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                updateProfitLabel(progress)
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) = Unit
        })

        loadPrefillData()
        binding.btnCalculate.setOnClickListener { calculatePrice() }
    }

    private fun loadPrefillData() {
        val productName = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        if (productName.isBlank()) return

        binding.etProductName.setText(productName)
        binding.etProductionCost.setText(intent.getLongExtra(EXTRA_PRODUCTION_COST, 0L).toString())
        binding.etOperationalCost.setText(intent.getLongExtra(EXTRA_OPERATIONAL_COST, 0L).toString())
        quantity = intent.getIntExtra(EXTRA_QUANTITY, 1).coerceAtLeast(1)
        val profit = intent.getDoubleExtra(EXTRA_PROFIT_PERCENTAGE, 25.0).toInt().coerceIn(0, 1000)
        binding.seekProfit.progress = profit
        updateQuantity()
        updateProfitLabel(profit)
    }

    private fun updateQuantity() {
        binding.tvQuantity.text = quantity.toString()
    }

    private fun updateProfitLabel(value: Int) {
        binding.tvProfitValue.text = "$value%"
    }

    private fun calculatePrice() {
        val productName = binding.etProductName.text.toString().trim()
        val productionCost = CurrencyUtils.parseCurrency(binding.etProductionCost.text.toString())
        val operationalCost = CurrencyUtils.parseCurrency(binding.etOperationalCost.text.toString())
        val profitPercentage = binding.seekProfit.progress.toDouble()

        when {
            productName.isBlank() -> {
                binding.etProductName.error = "Nama produk harus diisi"
                binding.etProductName.requestFocus()
            }
            productionCost + operationalCost <= 0L -> {
                Toast.makeText(this, "Isi minimal satu jenis biaya", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val result = CalculationHelper.calculate(
                    productName = productName,
                    productionCost = productionCost,
                    operationalCost = operationalCost,
                    quantity = quantity,
                    profitPercentage = profitPercentage
                )

                startActivity(Intent(this, CalculationResultActivity::class.java).apply {
                    putExtra(EXTRA_PRODUCT_NAME, result.productName)
                    putExtra(EXTRA_PRODUCTION_COST, result.productionCost)
                    putExtra(EXTRA_OPERATIONAL_COST, result.operationalCost)
                    putExtra(EXTRA_QUANTITY, result.quantity)
                    putExtra(EXTRA_PROFIT_PERCENTAGE, result.profitPercentage)
                    putExtra(EXTRA_TOTAL_COST, result.totalCost)
                    putExtra(EXTRA_COST_PER_ITEM, result.costPerItem)
                    putExtra(EXTRA_PROFIT_PER_ITEM, result.profitPerItem)
                    putExtra(EXTRA_SELLING_PRICE, result.sellingPrice)
                    putExtra(EXTRA_TOTAL_PROFIT, result.totalProfit)
                })
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_NAME = "product_name"
        const val EXTRA_PRODUCTION_COST = "production_cost"
        const val EXTRA_OPERATIONAL_COST = "operational_cost"
        const val EXTRA_QUANTITY = "quantity"
        const val EXTRA_PROFIT_PERCENTAGE = "profit_percentage"
        const val EXTRA_TOTAL_COST = "total_cost"
        const val EXTRA_COST_PER_ITEM = "cost_per_item"
        const val EXTRA_PROFIT_PER_ITEM = "profit_per_item"
        const val EXTRA_SELLING_PRICE = "selling_price"
        const val EXTRA_TOTAL_PROFIT = "total_profit"
    }
}
