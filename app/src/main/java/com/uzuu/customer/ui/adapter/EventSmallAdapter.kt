package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import java.text.NumberFormat
import java.util.Locale
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uzuu.customer.databinding.ItemEventGroupBinding
import com.uzuu.customer.domain.model.Event

class EventSmallAdapter(
    private val onClick: (Event) -> Unit
) : ListAdapter<Event, EventSmallAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
        }
        private val priceFormat = NumberFormat.getNumberInstance(Locale("vi", "VN"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEventGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.txtNameEventGroup.text = event.name
            binding.txtDateEventGroup.text = event.startTime ?: ""
            binding.txtPriceEventGroup.text = event.ticketTypes.minOfOrNull { it.price }
                ?.let { "Từ ${priceFormat.format(it.toLong())} đ" }
                ?: "Liên hệ"

            val imageUrl = EventAdapter.fixImageUrl(event.imageUrls.firstOrNull())
            Glide.with(binding.imgEventGroup).load(imageUrl).centerCrop().into(binding.imgEventGroup)

            binding.root.setOnClickListener { onClick(event) }
        }
    }
}
