package com.example.kalku.product

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kalku.R
import com.example.kalku.data.local.ProductEntity
import com.example.kalku.data.local.ProductStockStatus
import com.example.kalku.databinding.ItemProductBinding
import com.example.kalku.utils.CurrencyUtils

class ProductAdapter(
    private val onCalculate: (ProductEntity) -> Unit,
    private val onEdit: (ProductEntity) -> Unit,
    private val onDelete: (ProductEntity) -> Unit
) : ListAdapter<ProductEntity, ProductAdapter.ProductViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) = holder.bind(getItem(position))

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductEntity) = with(binding) {
            tvProductName.text = product.productName
            tvCategory.text = product.category
            tvSellingPrice.text = CurrencyUtils.formatRupiah(product.sellingPrice)
            tvQuantity.text = "${product.quantity} unit"
            tvProfit.text = "+${product.profitPercentage.toInt()}%"

            if (product.imageUri.isNotBlank()) {
                runCatching { ivProductImage.setImageURI(Uri.parse(product.imageUri)) }
                    .onFailure { ivProductImage.setImageResource(R.drawable.ic_storefront) }
            } else {
                ivProductImage.setImageResource(R.drawable.ic_storefront)
            }

            val (statusText, statusColor, statusBackground) = when (product.stockStatus()) {
                ProductStockStatus.ACTIVE -> Triple("Aktif", R.color.green_profit, R.drawable.bg_chip_green)
                ProductStockStatus.LOW_STOCK -> Triple("Stok Rendah", R.color.brown_primary, R.drawable.bg_chip_orange)
                ProductStockStatus.OUT_OF_STOCK -> Triple("Stok Habis", R.color.danger, R.drawable.bg_chip_red)
                ProductStockStatus.INACTIVE -> Triple("Nonaktif", R.color.text_gray_light, R.drawable.bg_chip_gray)
            }
            tvStatus.text = statusText
            tvStatus.setTextColor(ContextCompat.getColor(root.context, statusColor))
            tvStatus.setBackgroundResource(statusBackground)

            root.setOnClickListener { onCalculate(product) }
            btnCalculate.setOnClickListener { onCalculate(product) }
            btnEdit.setOnClickListener { onEdit(product) }
            btnDelete.setOnClickListener { onDelete(product) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<ProductEntity>() {
            override fun areItemsTheSame(oldItem: ProductEntity, newItem: ProductEntity) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: ProductEntity, newItem: ProductEntity) = oldItem == newItem
        }
    }
}
