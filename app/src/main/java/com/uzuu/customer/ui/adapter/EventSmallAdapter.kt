package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uzuu.customer.databinding.ItemEventInGroupBinding
import com.uzuu.customer.domain.model.Event

class EventSmallAdapter(
    private val onClick: (Event) -> Unit
) : ListAdapter<Event, EventSmallAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Event, newItem: Event) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventInGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEventInGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.txtNameEvent.text = event.name
            binding.txtAddress.text = event.location
            binding.txtDate.text = event.startTime ?: ""
            binding.txtPrice.text = event.ticketTypes.minOfOrNull { it.price }?.let { "Từ ${it} đ" } ?: "Liên hệ"

            val imageUrl = EventAdapter.fixImageUrl(event.imageUrls.firstOrNull())
            Glide.with(binding.imgEvent).load(imageUrl).centerCrop().into(binding.imgEvent)

            binding.root.setOnClickListener { onClick(event) }
        }
    }
}
