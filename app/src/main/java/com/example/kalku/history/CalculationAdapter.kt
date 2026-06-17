package com.example.kalku.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kalku.data.local.CalculationEntity
import com.example.kalku.databinding.ItemHistoryBinding
import com.example.kalku.utils.CurrencyUtils
import com.example.kalku.utils.DateUtils

class CalculationAdapter(
    private val showDeleteButton: Boolean = true,
    private val onClick: (CalculationEntity) -> Unit,
    private val onDelete: (CalculationEntity) -> Unit = {}
) : RecyclerView.Adapter<CalculationAdapter.CalculationViewHolder>() {

    private val items = mutableListOf<CalculationEntity>()

    fun submitList(calculations: List<CalculationEntity>) {
        items.clear()
        items.addAll(calculations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalculationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CalculationViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalculationEntity) = with(binding) {
            tvProductName.text = item.productName
            tvDate.text = DateUtils.formatDate(item.createdAt)
            tvSellingPrice.text = CurrencyUtils.formatRupiah(item.sellingPrice)
            tvProfit.text = "+${CurrencyUtils.formatRupiah(item.totalProfit)}"
            btnDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE
            root.setOnClickListener { onClick(item) }
            btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
