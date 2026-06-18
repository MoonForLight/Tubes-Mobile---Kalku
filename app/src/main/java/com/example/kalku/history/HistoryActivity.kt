package com.example.kalku.history

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalku.R
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.ActivityHistoryBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Calendar

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter
    private lateinit var database: AppDatabase
    private lateinit var sessionManager: SessionManager

    private var selectedFilter = HistoryFilter.ALL_TIME
    private var currentKeyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        sessionManager = SessionManager(this)

        setupRecyclerView()
        setupListeners()
        updateFilterButtonState()
        loadHistory()
    }

    private fun setupRecyclerView() {
        adapter = HistoryAdapter(
            onDeleteClick = { calculation -> showDeleteConfirmation(calculation) }
        )
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter
    }

    private fun setupListeners() = with(binding) {
        btnBack.setOnClickListener { finish() }

        chipAllTime.setOnClickListener {
            selectedFilter = HistoryFilter.ALL_TIME
            updateFilterButtonState()
            loadHistory()
        }

        chipThisMonth.setOnClickListener {
            selectedFilter = HistoryFilter.THIS_MONTH
            updateFilterButtonState()
            loadHistory()
        }

        chipLastThreeMonths.setOnClickListener {
            selectedFilter = HistoryFilter.LAST_THREE_MONTHS
            updateFilterButtonState()
            loadHistory()
        }

        etSearchHistory.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                currentKeyword = s?.toString()?.trim().orEmpty()
                loadHistory()
            }
        })
    }

    private fun loadHistory() {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            val startDate = selectedFilter.getStartDateMillis()
            val dao = database.calculationDao()

            val history = when {
                startDate != null && currentKeyword.isNotEmpty() -> {
                    dao.searchCalculationsByDate(userId, currentKeyword, startDate)
                }

                startDate != null -> {
                    dao.getCalculationsByDate(userId, startDate)
                }

                currentKeyword.isNotEmpty() -> {
                    dao.searchCalculationsByUser(userId, currentKeyword)
                }

                else -> {
                    dao.getCalculationsByUser(userId)
                }
            }

            adapter.submitList(history)
            updateSummary(history)
            updateEmptyState(history.isEmpty())
        }
    }

    private fun updateSummary(history: List<CalculationEntity>) = with(binding) {
        tvHistoryCount.text = "${history.size} riwayat"
        val totalProfit = history.sumOf { it.totalProfit }
        tvTotalProfit.text = CurrencyUtils.formatRupiah(totalProfit)
    }

    private fun updateEmptyState(isEmpty: Boolean) = with(binding) {
        layoutEmptyHistory.visibility = if (isEmpty) View.VISIBLE else View.GONE
        rvHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun updateFilterButtonState() = with(binding) {
        updateChipStyle(chipAllTime, selectedFilter == HistoryFilter.ALL_TIME)
        updateChipStyle(chipThisMonth, selectedFilter == HistoryFilter.THIS_MONTH)
        updateChipStyle(chipLastThreeMonths, selectedFilter == HistoryFilter.LAST_THREE_MONTHS)
    }

    private fun updateChipStyle(chip: TextView, isSelected: Boolean) {
        if (isSelected) {
            chip.setBackgroundResource(R.drawable.bg_chip_history_selected)
            chip.setTextColor(getColor(R.color.white))
        } else {
            chip.setBackgroundResource(R.drawable.bg_chip_history_unselected)
            chip.setTextColor(getColor(R.color.text_gray_light))
        }
    }

    private fun showDeleteConfirmation(calculation: CalculationEntity) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Riwayat")
            .setMessage("Hapus riwayat perhitungan ${calculation.productName}?")
            .setPositiveButton("Hapus") { _, _ -> deleteHistory(calculation.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteHistory(id: Int) {
        lifecycleScope.launch {
            database.calculationDao().deleteById(id)
            Toast.makeText(this@HistoryActivity, "Riwayat berhasil dihapus", Toast.LENGTH_SHORT).show()
            loadHistory()
        }
    }
}

enum class HistoryFilter {
    ALL_TIME,
    THIS_MONTH,
    LAST_THREE_MONTHS;

    fun getStartDateMillis(): Long? {
        return when (this) {
            ALL_TIME -> null
            THIS_MONTH -> Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            LAST_THREE_MONTHS -> Calendar.getInstance().apply {
                add(Calendar.MONTH, -3)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}
