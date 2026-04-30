package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uzuu.customer.databinding.ItemCheckoutTicketBinding
import com.uzuu.customer.domain.model.CartItem
import java.text.NumberFormat
import java.util.Locale

class CheckoutTicketAdapter :
    ListAdapter<CartItem, CheckoutTicketAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(old: CartItem, new: CartItem) = old.id == new.id
            override fun areContentsTheSame(old: CartItem, new: CartItem) = old == new
        }
        private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    }

    inner class VH(val binding: ItemCheckoutTicketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCheckoutTicketBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvEventName.text = item.eventName
            tvTicketType.text = item.ticketTypeName
            tvQuantity.text = "x${item.quantity}"
            tvUnitPrice.text = "${fmt.format(item.unitPrice.toLong())}d / ve"
            tvSubtotal.text = "${fmt.format(item.subtotal.toLong())}d"
        }
    }
}
