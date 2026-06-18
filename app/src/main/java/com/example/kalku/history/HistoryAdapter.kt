package com.example.kalku.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.ItemHistoryBinding
import com.example.kalku.utils.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onDeleteClick: (CalculationEntity) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val historyList = mutableListOf<CalculationEntity>()

    fun submitList(newList: List<CalculationEntity>) {
        historyList.clear()
        historyList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalculationEntity) = with(binding) {
            tvProductName.text = item.productName
            tvCalculationType.text = "COGS Calculation"
            tvCreatedAt.text = formatDate(item.createdAt)
            tvStatus.text = if (item.totalProfit > 0) "PROFITABLE" else "BREAKDOWN"
            tvSellingPrice.text = CurrencyUtils.formatRupiah(item.sellingPrice)
            tvTotalProfit.text = CurrencyUtils.formatRupiah(item.totalProfit)
            tvTotalCost.text = CurrencyUtils.formatRupiah(item.totalCost)
            tvQuantity.text = "${item.quantity} produk"
            tvMargin.text = "Margin ${item.profitPercentage.toInt()}%"
            btnDeleteHistory.setOnClickListener { onDeleteClick(item) }
        }
    }

    private fun formatDate(timeMillis: Long): String {
        val target = Calendar.getInstance().apply { timeInMillis = timeMillis }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val timeFormat = SimpleDateFormat("HH:mm", Locale("in", "ID"))
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))

        return when {
            isSameDay(target, today) -> "Hari ini, ${timeFormat.format(Date(timeMillis))}"
            isSameDay(target, yesterday) -> "Kemarin, ${timeFormat.format(Date(timeMillis))}"
            else -> dateFormat.format(Date(timeMillis))
        }
    }

    private fun isSameDay(first: Calendar, second: Calendar): Boolean {
        return first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
            first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
    }
}
