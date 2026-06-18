package com.example.kalku.product

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalku.calculator.CalculatorActivity
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.ProductEntity
import com.example.kalku.data.local.ProductStockStatus
import com.example.kalku.databinding.FragmentProductBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter
    private var allProducts: List<ProductEntity> = emptyList()

    private val statusFilters = listOf("Semua Status", "Aktif", "Stok Rendah", "Habis", "Nonaktif")
    private val sortOptions = listOf("Terbaru", "Nama A–Z", "Harga Tertinggi", "Harga Terendah", "Stok Terendah")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ProductAdapter(
            onCalculate = ::openCalculator,
            onEdit = ::editProduct,
            onDelete = ::confirmDelete
        )

        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = adapter
        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), ProductFormActivity::class.java))
        }

        binding.spinnerStatusFilter.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            statusFilters
        )
        binding.spinnerSort.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            sortOptions
        )
        binding.spinnerStatusFilter.onItemSelectedListener = SimpleItemSelectedListener { applyFilters() }
        binding.spinnerSort.onItemSelectedListener = SimpleItemSelectedListener { applyFilters() }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = applyFilters()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val userId = SessionManager(requireContext()).getUserId()
            allProducts = database.productDao().getProductsByUser(userId)

            binding.tvActiveCount.text = database.productDao().countActiveProducts(userId).toString()
            binding.tvLowStockCount.text = database.productDao().countLowStockProducts(userId).toString()
            binding.tvInventoryValue.text = CurrencyUtils.formatRupiah(
                database.productDao().getPotentialSalesValue(userId)
            )
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (!this::adapter.isInitialized || _binding == null) return

        val keyword = binding.etSearch.text.toString().trim()
        val statusIndex = binding.spinnerStatusFilter.selectedItemPosition
        val sortIndex = binding.spinnerSort.selectedItemPosition

        val filtered = allProducts
            .asSequence()
            .filter {
                keyword.isBlank() ||
                    it.productName.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true)
            }
            .filter { product ->
                when (statusIndex) {
                    1 -> product.stockStatus() == ProductStockStatus.ACTIVE
                    2 -> product.stockStatus() == ProductStockStatus.LOW_STOCK
                    3 -> product.stockStatus() == ProductStockStatus.OUT_OF_STOCK
                    4 -> product.stockStatus() == ProductStockStatus.INACTIVE
                    else -> true
                }
            }
            .toList()
            .let { list ->
                when (sortIndex) {
                    1 -> list.sortedBy { it.productName.lowercase() }
                    2 -> list.sortedByDescending { it.sellingPrice }
                    3 -> list.sortedBy { it.sellingPrice }
                    4 -> list.sortedBy { it.quantity }
                    else -> list.sortedByDescending { it.updatedAt }
                }
            }

        adapter.submitList(filtered)
        binding.emptyState.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        binding.rvProducts.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
        binding.tvProductCount.text = filtered.size.toString()
    }

    private fun openCalculator(product: ProductEntity) {
        startActivity(Intent(requireContext(), CalculatorActivity::class.java).apply {
            putExtra(CalculatorActivity.EXTRA_PRODUCT_ID, product.id)
            putExtra(CalculatorActivity.EXTRA_PRODUCT_NAME, product.productName)
            putExtra(CalculatorActivity.EXTRA_PRODUCTION_COST, product.productionCost)
            putExtra(CalculatorActivity.EXTRA_OPERATIONAL_COST, product.operationalCost)
            putExtra(CalculatorActivity.EXTRA_QUANTITY, product.quantity.coerceAtLeast(1))
            putExtra(CalculatorActivity.EXTRA_PROFIT_PERCENTAGE, product.profitPercentage)
        })
    }

    private fun editProduct(product: ProductEntity) {
        startActivity(Intent(requireContext(), ProductFormActivity::class.java).apply {
            putExtra(ProductFormActivity.EXTRA_PRODUCT_ID, product.id)
        })
    }

    private fun confirmDelete(product: ProductEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus produk")
            .setMessage("Produk ${product.productName} akan dihapus. Riwayat hitung lama tetap disimpan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AppDatabase.getDatabase(requireContext()).productDao().delete(product)
                    loadProducts()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
