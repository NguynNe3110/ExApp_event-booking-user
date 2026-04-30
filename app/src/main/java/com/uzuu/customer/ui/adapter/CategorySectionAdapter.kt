package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.uzuu.customer.databinding.ItemCategorySectionBinding
import com.uzuu.customer.feature.middle.home.CategoryWithEvents

class CategorySectionAdapter(
    private val onEventClick: (com.uzuu.customer.domain.model.Event) -> Unit,
    private val onViewMoreClick: (String) -> Unit
) : ListAdapter<CategoryWithEvents, CategorySectionAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CategoryWithEvents>() {
            override fun areItemsTheSame(oldItem: CategoryWithEvents, newItem: CategoryWithEvents) = oldItem.categoryName == newItem.categoryName
            override fun areContentsTheSame(oldItem: CategoryWithEvents, newItem: CategoryWithEvents) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategorySectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCategorySectionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val innerAdapter = EventSmallAdapter(onEventClick)

        fun bind(item: CategoryWithEvents) {
            binding.includeHeader.tvCategoryName.text = item.categoryName
            binding.includeHeader.btnViewMore.visibility = if (item.hasMoreEvents) View.VISIBLE else View.GONE
            binding.includeHeader.btnViewMore.setOnClickListener { onViewMoreClick(item.categoryName) }

            binding.recyclerInnerEvents.apply {
                layoutManager = androidx.recyclerview.widget.GridLayoutManager(context, 2, androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false)
                adapter = innerAdapter
                setHasFixedSize(true)
            }

            innerAdapter.submitList(item.displayedEvents)
        }
    }
}
