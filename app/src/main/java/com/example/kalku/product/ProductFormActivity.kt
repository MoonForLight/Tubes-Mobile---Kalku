package com.example.kalku.product

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kalku.calculator.CalculationHelper
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.ProductEntity
import com.example.kalku.databinding.ActivityProductFormBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class ProductFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductFormBinding
    private var productId = 0
    private var existingProduct: ProductEntity? = null

    private val categories = listOf(
        "Makanan & Minuman",
        "Fashion",
        "Aksesoris",
        "Kosmetik",
        "Elektronik",
        "Kerajinan",
        "Lainnya"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)
        binding.spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        binding.btnBack.setOnClickListener { finish() }
        binding.btnSaveProduct.setOnClickListener { saveProduct() }

        if (productId > 0) {
            binding.tvTitle.text = "Edit Produk"
            binding.btnSaveProduct.text = "Simpan Perubahan"
            loadProduct()
        }
    }

    private fun loadProduct() {
        lifecycleScope.launch {
            existingProduct = AppDatabase.getDatabase(this@ProductFormActivity)
                .productDao()
                .getProductById(productId)

            existingProduct?.let { product ->
                binding.etProductName.setText(product.productName)
                binding.etProductionCost.setText(product.productionCost.toString())
                binding.etOperationalCost.setText(product.operationalCost.toString())
                binding.etQuantity.setText(product.quantity.toString())
                binding.etProfit.setText(product.profitPercentage.toInt().toString())
                val position = categories.indexOf(product.category).coerceAtLeast(0)
                binding.spinnerCategory.setSelection(position)
            }
        }
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val productionCost = CurrencyUtils.parseCurrency(binding.etProductionCost.text.toString())
        val operationalCost = CurrencyUtils.parseCurrency(binding.etOperationalCost.text.toString())
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        val profit = binding.etProfit.text.toString().toDoubleOrNull() ?: -1.0
        val category = categories[binding.spinnerCategory.selectedItemPosition]

        when {
            name.isBlank() -> binding.etProductName.error = "Nama produk harus diisi"
            productionCost + operationalCost <= 0L -> Toast.makeText(this, "Isi minimal satu biaya", Toast.LENGTH_SHORT).show()
            quantity <= 0 -> binding.etQuantity.error = "Jumlah harus lebih dari 0"
            profit < 0 || profit > 1000 -> binding.etProfit.error = "Keuntungan harus 0–1000%"
            else -> lifecycleScope.launch {
                val result = CalculationHelper.calculate(
                    productName = name,
                    productionCost = productionCost,
                    operationalCost = operationalCost,
                    quantity = quantity,
                    profitPercentage = profit
                )

                val now = System.currentTimeMillis()
                val product = ProductEntity(
                    id = existingProduct?.id ?: 0,
                    userId = SessionManager(this@ProductFormActivity).getUserId(),
                    productName = name,
                    category = category,
                    productionCost = productionCost,
                    operationalCost = operationalCost,
                    quantity = quantity,
                    profitPercentage = profit,
                    sellingPrice = result.sellingPrice,
                    totalProfit = result.totalProfit,
                    createdAt = existingProduct?.createdAt ?: now,
                    updatedAt = now
                )

                val dao = AppDatabase.getDatabase(this@ProductFormActivity).productDao()
                if (existingProduct == null) dao.insert(product) else dao.update(product)

                Toast.makeText(
                    this@ProductFormActivity,
                    if (existingProduct == null) "Produk berhasil ditambahkan" else "Produk berhasil diperbarui",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "product_id"
    }
}
