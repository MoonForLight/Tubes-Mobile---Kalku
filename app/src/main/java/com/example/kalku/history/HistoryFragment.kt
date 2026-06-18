package com.example.kalku.history

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.FragmentHistoryBinding
import com.example.kalku.utils.DateUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Calendar

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CalculationAdapter
    private var allItems: List<CalculationEntity> = emptyList()
    private var visibleItems: List<CalculationEntity> = emptyList()
    private var customStart: Long? = null
    private var customEnd: Long? = null

    private val filters = listOf("Semua", "7 Hari", "30 Hari", "90 Hari", "Rentang Tanggal")

    private val createCsv = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri ?: return@registerForActivityResult
        runCatching {
            requireContext().contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.appendLine("Produk,Tanggal,Biaya Produksi,Biaya Operasional,Jumlah,Margin,Harga Jual,Total Profit")
                visibleItems.forEach { item ->
                    writer.appendLine(
                        listOf(
                            csv(item.productName),
                            csv(DateUtils.formatDate(item.createdAt)),
                            item.productionCost,
                            item.operationalCost,
                            item.quantity,
                            item.profitPercentage,
                            item.sellingPrice,
                            item.totalProfit
                        ).joinToString(",")
                    )
                }
            }
        }.onSuccess {
            Toast.makeText(requireContext(), "Riwayat berhasil diekspor", Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(requireContext(), "Gagal mengekspor riwayat", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CalculationAdapter(
            showDeleteButton = true,
            onClick = { calculation ->
                startActivity(Intent(requireContext(), HistoryDetailActivity::class.java).apply {
                    putExtra(HistoryDetailActivity.EXTRA_CALCULATION_ID, calculation.id)
                })
            },
            onDelete = ::confirmDelete
        )

        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        binding.spinnerDateFilter.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            filters
        )
        binding.spinnerDateFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 4 && customStart == null) showCustomDateRange() else applyFilters()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.btnExport.setOnClickListener {
            if (visibleItems.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            } else {
                createCsv.launch("riwayat-kalku-${System.currentTimeMillis()}.csv")
            }
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = applyFilters()
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = SessionManager(requireContext()).getUserId()
            allItems = AppDatabase.getDatabase(requireContext()).calculationDao().getCalculationsByUser(userId)
            applyFilters()
        }
    }

    private fun applyFilters() {
        if (_binding == null) return
        val keyword = binding.etSearch.text.toString().trim()
        val now = System.currentTimeMillis()
        val day = 24L * 60L * 60L * 1000L
        val selected = binding.spinnerDateFilter.selectedItemPosition
        val range = when (selected) {
            1 -> now - 7 * day to now
            2 -> now - 30 * day to now
            3 -> now - 90 * day to now
            4 -> (customStart ?: Long.MIN_VALUE) to (customEnd ?: Long.MAX_VALUE)
            else -> Long.MIN_VALUE to Long.MAX_VALUE
        }

        visibleItems = allItems.filter { item ->
            (keyword.isBlank() || item.productName.contains(keyword, ignoreCase = true)) &&
                item.createdAt in range.first..range.second
        }
        adapter.submitList(visibleItems)
        binding.emptyState.visibility = if (visibleItems.isEmpty()) View.VISIBLE else View.GONE
        binding.rvHistory.visibility = if (visibleItems.isEmpty()) View.GONE else View.VISIBLE
        binding.tvHistoryCount.text = "${visibleItems.size} perhitungan"
    }

    private fun showCustomDateRange() {
        val today = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, startYear, startMonth, startDay ->
                val start = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                DatePickerDialog(
                    requireContext(),
                    { _, endYear, endMonth, endDay ->
                        val end = Calendar.getInstance().apply {
                            set(endYear, endMonth, endDay, 23, 59, 59)
                            set(Calendar.MILLISECOND, 999)
                        }
                        if (end.timeInMillis < start.timeInMillis) {
                            Toast.makeText(requireContext(), "Tanggal akhir tidak boleh sebelum tanggal awal", Toast.LENGTH_SHORT).show()
                            binding.spinnerDateFilter.setSelection(0)
                        } else {
                            customStart = start.timeInMillis
                            customEnd = end.timeInMillis
                            applyFilters()
                        }
                    },
                    today.get(Calendar.YEAR),
                    today.get(Calendar.MONTH),
                    today.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun confirmDelete(calculation: CalculationEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus riwayat")
            .setMessage("Riwayat ${calculation.productName} akan dihapus.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AppDatabase.getDatabase(requireContext()).calculationDao().delete(calculation)
                    loadHistory()
                }
            }
            .show()
    }

    private fun csv(value: String): String = "\"${value.replace("\"", "\"\"")}\""

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
