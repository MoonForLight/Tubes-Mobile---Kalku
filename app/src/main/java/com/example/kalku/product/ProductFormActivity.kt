package com.example.kalku.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kalku.R
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
    private var selectedImageUri: String = ""

    private val categories = listOf(
        "Makanan & Minuman",
        "Fashion",
        "Aksesoris",
        "Kosmetik",
        "Elektronik",
        "Kerajinan",
        "Lainnya"
    )

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri ?: return@registerForActivityResult
        runCatching {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        selectedImageUri = uri.toString()
        showSelectedImage(uri)
    }

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
        binding.btnChooseImage.setOnClickListener { imagePicker.launch(arrayOf("image/*")) }
        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = ""
            binding.ivProductImage.setImageResource(R.drawable.ic_storefront)
            binding.btnRemoveImage.isVisible = false
        }
        binding.btnSaveProduct.setOnClickListener { saveProduct() }

        if (productId > 0) {
            binding.tvTitle.text = "Edit Produk"
            binding.btnSaveProduct.text = "Simpan Perubahan"
            loadProduct()
        }
    }

    private fun loadProduct() {
        lifecycleScope.launch {
            val userId = SessionManager(this@ProductFormActivity).getUserId()
            existingProduct = AppDatabase.getDatabase(this@ProductFormActivity)
                .productDao()
                .getProductById(productId, userId)

            val product = existingProduct
            if (product == null) {
                Toast.makeText(this@ProductFormActivity, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            binding.etProductName.setText(product.productName)
            binding.etProductionCost.setText(product.productionCost.toString())
            binding.etOperationalCost.setText(product.operationalCost.toString())
            binding.etQuantity.setText(product.quantity.toString())
            binding.etProfit.setText(product.profitPercentage.toInt().toString())
            binding.etLowStockThreshold.setText(product.lowStockThreshold.toString())
            binding.switchActive.isChecked = product.isActive
            binding.spinnerCategory.setSelection(categories.indexOf(product.category).coerceAtLeast(0))
            selectedImageUri = product.imageUri
            if (selectedImageUri.isNotBlank()) showSelectedImage(Uri.parse(selectedImageUri))
        }
    }

    private fun showSelectedImage(uri: Uri) {
        binding.ivProductImage.setImageURI(uri)
        binding.btnRemoveImage.isVisible = true
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val productionCost = CurrencyUtils.parseCurrency(binding.etProductionCost.text.toString())
        val operationalCost = CurrencyUtils.parseCurrency(binding.etOperationalCost.text.toString())
        val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0
        val profit = binding.etProfit.text.toString().toDoubleOrNull() ?: -1.0
        val lowStockThreshold = binding.etLowStockThreshold.text.toString().toIntOrNull() ?: 5
        val category = categories[binding.spinnerCategory.selectedItemPosition]

        when {
            name.length < 2 -> binding.etProductName.error = "Nama produk minimal 2 karakter"
            productionCost + operationalCost <= 0L -> Toast.makeText(this, "Isi minimal satu biaya", Toast.LENGTH_SHORT).show()
            quantity < 0 -> binding.etQuantity.error = "Jumlah tidak boleh negatif"
            profit !in 0.0..100.0 -> binding.etProfit.error = "Keuntungan harus 0–100%"
            lowStockThreshold < 0 -> binding.etLowStockThreshold.error = "Batas stok tidak boleh negatif"
            else -> lifecycleScope.launch {
                val safeQuantity = quantity.coerceAtLeast(1)
                val result = CalculationHelper.calculate(
                    productName = name,
                    productionCost = productionCost,
                    operationalCost = operationalCost,
                    quantity = safeQuantity,
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
                    totalProfit = if (quantity > 0) result.totalProfit else 0L,
                    imageUri = selectedImageUri,
                    isActive = binding.switchActive.isChecked,
                    lowStockThreshold = lowStockThreshold,
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
