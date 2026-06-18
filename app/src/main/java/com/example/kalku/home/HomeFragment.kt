package com.example.kalku.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalku.MainActivity
import com.example.kalku.R
import com.example.kalku.calculator.CalculatorActivity
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.FragmentHomeBinding
import com.example.kalku.history.CalculationAdapter
import com.example.kalku.history.HistoryDetailActivity
import com.example.kalku.product.ProductFormActivity
import com.example.kalku.utils.AppSettingsManager
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentAdapter: CalculationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recentAdapter = CalculationAdapter(
            showDeleteButton = false,
            onClick = { calculation ->
                startActivity(Intent(requireContext(), HistoryDetailActivity::class.java).apply {
                    putExtra(HistoryDetailActivity.EXTRA_CALCULATION_ID, calculation.id)
                })
            }
        )

        binding.rvRecent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecent.adapter = recentAdapter
        binding.rvRecent.isNestedScrollingEnabled = false

        binding.btnCalculate.setOnClickListener { startActivity(Intent(requireContext(), CalculatorActivity::class.java)) }
        binding.btnAddProduct.setOnClickListener { startActivity(Intent(requireContext(), ProductFormActivity::class.java)) }
        binding.tvSeeAll.setOnClickListener { (activity as? MainActivity)?.openTab(R.id.nav_history) }
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        val session = SessionManager(requireContext())
        binding.tvGreeting.text = "Halo, ${session.getFullName().substringBefore(' ')}!"
        binding.tvBusinessName.text = session.getBusinessName()

        viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            val userId = session.getUserId()
            val activeCount = database.productDao().countActiveProducts(userId)
            val lowStockCount = database.productDao().countLowStockProducts(userId)
            val productProfit = database.productDao().getEstimatedProfit(userId)
            val calculationProfit = database.calculationDao().getTotalProfitByUser(userId)
            val recent = database.calculationDao().getRecentCalculations(userId, 3)
            val allCalculations = database.calculationDao().getCalculationsByUser(userId)

            binding.tvProductCount.text = activeCount.toString()
            binding.tvLowStockSummary.text = "$lowStockCount stok rendah"
            val displayedProfit = if (calculationProfit > 0L) calculationProfit else productProfit
            binding.tvEstimatedProfit.text = CurrencyUtils.formatRupiah(displayedProfit)
            binding.tvProfitSource.text = if (calculationProfit > 0L) "profit riwayat tersimpan" else "estimasi dari produk aktif"
            binding.profitTrend.setValues(buildSevenDayTrend(allCalculations))
            val startToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
            val hasTodayCalculation = allCalculations.any { it.createdAt >= startToday }
            binding.tvReminder.visibility = if (
                AppSettingsManager(requireContext()).isReminderEnabled() && !hasTodayCalculation
            ) View.VISIBLE else View.GONE

            recentAdapter.submitList(recent)
            binding.emptyRecent.visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRecent.visibility = if (recent.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun buildSevenDayTrend(items: List<CalculationEntity>): List<Long> {
        val startToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val day = 24L * 60L * 60L * 1000L
        return (6 downTo 0).map { daysAgo ->
            val start = startToday - daysAgo * day
            val end = start + day - 1
            items.filter { it.createdAt in start..end }.sumOf { it.totalProfit }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
