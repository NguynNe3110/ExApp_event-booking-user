package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uzuu.customer.R
import com.uzuu.customer.databinding.ItemBlogBinding
import com.uzuu.customer.domain.model.BlogEvent

class BlogAdapter : ListAdapter<BlogEvent, BlogAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<BlogEvent>() {
            override fun areItemsTheSame(oldItem: BlogEvent, newItem: BlogEvent) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BlogEvent, newItem: BlogEvent) =
                oldItem == newItem
        }
    }

    inner class VH(val binding: ItemBlogBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemBlogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            tvBlogTitle.text = item.name
            tvBlogLocation.text = item.location.ifBlank { item.province }
            tvBlogTime.text = listOfNotNull(item.startTime, item.endTime)
                .joinToString(" - ")
                .ifBlank { "Chua xac dinh thoi gian" }
            tvBlogStatus.text = item.descriptionStatus.orEmpty()

            Glide.with(imgBlog)
                .load(EventAdapter.fixImageUrl(item.imageUrl))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .centerCrop()
                .into(imgBlog)
        }
    }
}
