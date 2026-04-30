package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.uzuu.customer.databinding.ItemCategoryHeaderBinding
import com.uzuu.customer.databinding.ItemEventInGroupBinding
import com.uzuu.customer.domain.model.Event
import com.uzuu.customer.feature.middle.home.CategoryWithEvents

sealed class CategoryEventItem {
    data class CategoryHeader(
        val categoryId: Long,
        val categoryName: String,
        val totalCount: Int,
        val hasMore: Boolean
    ) : CategoryEventItem()

    data class EventItem(val event: Event) : CategoryEventItem()

    data class SuggestionSection(val events: List<Event>) : CategoryEventItem()
}

class CategoryEventsAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onViewMoreClick: (categoryName: String) -> Unit
) : ListAdapter<CategoryEventItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_CATEGORY_HEADER = 0
        private const val TYPE_EVENT = 1
        private const val TYPE_SUGGESTION = 2

        private val DIFF = object : DiffUtil.ItemCallback<CategoryEventItem>() {
            override fun areItemsTheSame(oldItem: CategoryEventItem, newItem: CategoryEventItem): Boolean {
                return when {
                    oldItem is CategoryEventItem.CategoryHeader && newItem is CategoryEventItem.CategoryHeader ->
                        oldItem.categoryId == newItem.categoryId
                    oldItem is CategoryEventItem.EventItem && newItem is CategoryEventItem.EventItem ->
                        oldItem.event.id == newItem.event.id
                    oldItem is CategoryEventItem.SuggestionSection && newItem is CategoryEventItem.SuggestionSection ->
                        true
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: CategoryEventItem, newItem: CategoryEventItem) =
                oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CategoryEventItem.CategoryHeader -> TYPE_CATEGORY_HEADER
            is CategoryEventItem.EventItem -> TYPE_EVENT
            is CategoryEventItem.SuggestionSection -> TYPE_SUGGESTION
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY_HEADER -> {
                val binding = ItemCategoryHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                CategoryHeaderViewHolder(binding, onViewMoreClick)
            }
            TYPE_EVENT -> {
                val binding = ItemEventInGroupBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                EventViewHolder(binding, onEventClick)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is CategoryEventItem.CategoryHeader -> {
                (holder as CategoryHeaderViewHolder).bind(item)
            }
            is CategoryEventItem.EventItem -> {
                (holder as EventViewHolder).bind(item.event)
            }
            is CategoryEventItem.SuggestionSection -> {
                // Handled in a special way if needed
            }
        }
    }

    inner class CategoryHeaderViewHolder(
        private val binding: ItemCategoryHeaderBinding,
        private val onViewMore: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryEventItem.CategoryHeader) {
            binding.tvCategoryName.text = item.categoryName
            binding.btnViewMore.setOnClickListener {
                onViewMore(item.categoryName)
            }
        }
    }

    inner class EventViewHolder(
        private val binding: ItemEventInGroupBinding,
        private val onEventClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.txtNameEvent.text = event.name
            binding.txtAddress.text = event.location
            binding.txtPrice.text = event.ticketTypes.firstOrNull()?.let {
                "${it.price}₫"
            } ?: "Liên hệ"
            binding.txtDate.text = event.startTime ?: "Chưa xác định"

            val imageUrl = EventAdapter.fixImageUrl(event.imageUrls.firstOrNull())
            com.bumptech.glide.Glide.with(binding.imgEvent)
                .load(imageUrl)
                .centerCrop()
                .into(binding.imgEvent)

            binding.root.setOnClickListener {
                onEventClick(event)
            }
        }
    }
}
