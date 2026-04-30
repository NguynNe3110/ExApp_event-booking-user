package com.uzuu.customer.feature.middle.blog

import com.uzuu.customer.domain.model.BlogEvent

data class BlogUiState(
    val isLoading: Boolean = false,
    val blogs: List<BlogEvent> = emptyList(),
    val error: String? = null
)
