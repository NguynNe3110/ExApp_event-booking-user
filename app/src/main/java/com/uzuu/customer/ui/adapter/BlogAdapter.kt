package com.uzuu.customer.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.uzuu.customer.R
import com.uzuu.customer.databinding.ItemBlogBinding
import com.uzuu.customer.domain.model.BlogPost

class BlogAdapter : ListAdapter<BlogPost, BlogAdapter.VH>(DIFF) {
    var onItemClick: ((BlogPost) -> Unit)? = null

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<BlogPost>() {
            override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost) =
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
            tvBlogTitle.text = item.title
            tvBlogLocation.text = item.authorName.orEmpty()
            tvBlogTime.text = item.publishedAt ?: item.createdAt ?: ""
            tvBlogStatus.text = item.summary.orEmpty()

            Glide.with(imgBlog)
                .load(EventAdapter.fixImageUrl(item.thumbnail))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .centerCrop()
                .into(imgBlog)

            imgMore.setOnClickListener {
                // placeholder action
                android.widget.Toast.makeText(it.context, "Menu", android.widget.Toast.LENGTH_SHORT).show()
            }

            btnLike.setOnClickListener {
                btnLike.text = "Đã thích"
            }

            btnComment.setOnClickListener {
                android.widget.Toast.makeText(it.context, "Mở phần bình luận", android.widget.Toast.LENGTH_SHORT).show()
            }

            btnShare.setOnClickListener {
                android.widget.Toast.makeText(it.context, "Chia sẻ", android.widget.Toast.LENGTH_SHORT).show()
            }

            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
}
