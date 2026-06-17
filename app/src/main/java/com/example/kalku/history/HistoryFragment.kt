package com.example.kalku.history

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
import com.example.kalku.data.local.AppDatabase
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.FragmentHistoryBinding
import com.example.kalku.utils.SessionManager
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CalculationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadHistory(s?.toString().orEmpty())
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    override fun onResume() {
        super.onResume()
        loadHistory(binding.etSearch.text.toString())
    }

    private fun loadHistory(keyword: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val dao = AppDatabase.getDatabase(requireContext()).calculationDao()
            val userId = SessionManager(requireContext()).getUserId()
            val items = if (keyword.isBlank()) {
                dao.getCalculationsByUser(userId)
            } else {
                dao.searchCalculations(userId, keyword.trim())
            }

            adapter.submitList(items)
            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.rvHistory.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
            binding.tvHistoryCount.text = "${items.size} perhitungan"
        }
    }

    private fun confirmDelete(calculation: CalculationEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus riwayat")
            .setMessage("Riwayat ${calculation.productName} akan dihapus.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AppDatabase.getDatabase(requireContext()).calculationDao().delete(calculation)
                    loadHistory(binding.etSearch.text.toString())
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
