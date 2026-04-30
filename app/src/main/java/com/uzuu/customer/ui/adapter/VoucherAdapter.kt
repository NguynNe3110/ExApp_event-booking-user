package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uzuu.customer.databinding.ItemVoucherBinding
import com.uzuu.customer.domain.model.Voucher
import java.text.NumberFormat
import java.util.Locale

class VoucherAdapter(
    private val onClick: (Voucher) -> Unit
) : ListAdapter<Voucher, VoucherAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Voucher>() {
            override fun areItemsTheSame(old: Voucher, new: Voucher) = old.id == new.id
            override fun areContentsTheSame(old: Voucher, new: Voucher) = old == new
        }
        private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    }

    inner class VH(val binding: ItemVoucherBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val voucher = getItem(position)
        with(holder.binding) {
            tvCode.text = voucher.code
            tvOrganizer.text = "Organizer: ${voucher.creatorName.orEmpty().ifBlank { "Khong ro" }}"
            tvEvent.text = voucher.eventName?.takeIf { it.isNotBlank() } ?: "Ap dung theo dieu kien voucher"
            tvDiscount.text = discountText(voucher)
            root.setOnClickListener { onClick(voucher) }
        }
    }

    private fun discountText(voucher: Voucher): String {
        val type = voucher.discountType.uppercase(Locale.US)
        return if (type.contains("PERCENT")) {
            val cap = voucher.maxDiscount?.takeIf { it > 0 }?.let {
                ", toi da ${fmt.format(it.toLong())}d"
            }.orEmpty()
            "Giam ${voucher.amount.toInt()}%$cap"
        } else {
            "Giam ${fmt.format(voucher.amount.toLong())}d"
        }
    }
}
