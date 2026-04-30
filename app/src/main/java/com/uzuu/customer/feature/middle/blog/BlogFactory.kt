package com.uzuu.customer.feature.middle.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.BlogRepository

class BlogFactory(
    private val blogRepo: BlogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BlogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BlogViewModel(blogRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
