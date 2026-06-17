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
import com.example.kalku.databinding.FragmentHomeBinding
import com.example.kalku.history.CalculationAdapter
import com.example.kalku.history.HistoryDetailActivity
import com.example.kalku.product.ProductFormActivity
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentAdapter: CalculationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        binding.btnCalculate.setOnClickListener {
            startActivity(Intent(requireContext(), CalculatorActivity::class.java))
        }
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(requireContext(), ProductFormActivity::class.java))
        }
        binding.tvSeeAll.setOnClickListener {
            (activity as? MainActivity)?.openTab(R.id.nav_history)
        }
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
            val productCount = database.productDao().countProducts(userId)
            val productProfit = database.productDao().getEstimatedProfit(userId)
            val calculationProfit = database.calculationDao().getTotalProfitByUser(userId)
            val recent = database.calculationDao().getRecentCalculations(userId, 3)

            binding.tvProductCount.text = productCount.toString()
            binding.tvEstimatedProfit.text = CurrencyUtils.formatRupiah(maxOf(productProfit, calculationProfit))
            recentAdapter.submitList(recent)
            binding.emptyRecent.visibility = if (recent.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRecent.visibility = if (recent.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
