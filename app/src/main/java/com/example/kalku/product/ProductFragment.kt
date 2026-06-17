package com.example.kalku.product

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalku.calculator.CalculatorActivity
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.ProductEntity
import com.example.kalku.databinding.FragmentProductBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductAdapter

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

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadProducts(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        loadProducts(binding.etSearch.text.toString())
    }

    private fun loadProducts(keyword: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val userId = SessionManager(requireContext()).getUserId()
            val products = if (keyword.isBlank()) {
                database.productDao().getProductsByUser(userId)
            } else {
                database.productDao().searchProducts(userId, keyword.trim())
            }

            adapter.submitList(products)
            binding.emptyState.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            binding.rvProducts.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
            binding.tvProductCount.text = products.size.toString()
            binding.tvInventoryValue.text = CurrencyUtils.formatRupiah(
                database.productDao().getInventoryValue(userId)
            )
        }
    }

    private fun openCalculator(product: ProductEntity) {
        val intent = Intent(requireContext(), CalculatorActivity::class.java).apply {
            putExtra(CalculatorActivity.EXTRA_PRODUCT_NAME, product.productName)
            putExtra(CalculatorActivity.EXTRA_PRODUCTION_COST, product.productionCost)
            putExtra(CalculatorActivity.EXTRA_OPERATIONAL_COST, product.operationalCost)
            putExtra(CalculatorActivity.EXTRA_QUANTITY, product.quantity)
            putExtra(CalculatorActivity.EXTRA_PROFIT_PERCENTAGE, product.profitPercentage)
        }
        startActivity(intent)
    }

    private fun editProduct(product: ProductEntity) {
        startActivity(Intent(requireContext(), ProductFormActivity::class.java).apply {
            putExtra(ProductFormActivity.EXTRA_PRODUCT_ID, product.id)
        })
    }

    private fun confirmDelete(product: ProductEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus produk")
            .setMessage("Produk ${product.productName} akan dihapus. Lanjutkan?")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AppDatabase.getDatabase(requireContext()).productDao().delete(product)
                    loadProducts(binding.etSearch.text.toString())
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
