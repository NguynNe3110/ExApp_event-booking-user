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

    var selectedVoucherId: Long? = null
        set(value) {
            field = value
            notifyDataSetChanged()
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
            tvOrganizer.text = "Tổ chức: ${voucher.creatorName.orEmpty().ifBlank { "Khong ro" }}"
            tvEvent.text = "Sự kiện: ${voucher.eventName?.takeIf { it.isNotBlank() } ?: "Khong ro"}"
            tvDiscount.text = discountText(voucher)

            checkboxSelect.setOnCheckedChangeListener(null)
            checkboxSelect.isChecked = voucher.id == selectedVoucherId
            checkboxSelect.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    selectedVoucherId = voucher.id
                    onClick(voucher)
                } else if (selectedVoucherId == voucher.id) {
                    selectedVoucherId = null
                }
            }

            root.setOnClickListener { checkboxSelect.performClick() }
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
