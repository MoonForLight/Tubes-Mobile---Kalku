package com.example.kalku.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kalku.data.local.ProductEntity
import com.example.kalku.databinding.ItemProductBinding
import com.example.kalku.utils.CurrencyUtils

class ProductAdapter(
    private val onCalculate: (ProductEntity) -> Unit,
    private val onEdit: (ProductEntity) -> Unit,
    private val onDelete: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val items = mutableListOf<ProductEntity>()

    fun submitList(products: List<ProductEntity>) {
        items.clear()
        items.addAll(products)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) = with(binding) {
            tvProductName.text = product.productName
            tvCategory.text = product.category
            tvSellingPrice.text = CurrencyUtils.formatRupiah(product.sellingPrice)
            tvQuantity.text = "${product.quantity} unit"
            tvProfit.text = "+${product.profitPercentage.toInt()}%"

            root.setOnClickListener { onCalculate(product) }
            btnCalculate.setOnClickListener { onCalculate(product) }
            btnEdit.setOnClickListener { onEdit(product) }
            btnDelete.setOnClickListener { onDelete(product) }
        }
    }
}
