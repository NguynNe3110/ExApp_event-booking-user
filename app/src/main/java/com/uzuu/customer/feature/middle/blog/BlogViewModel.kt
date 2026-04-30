package com.uzuu.customer.feature.middle.blog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.domain.repository.BlogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BlogViewModel(
    private val blogRepo: BlogRepository
) : ViewModel() {

    private val _blogState = MutableStateFlow(BlogUiState())
    val blogState = _blogState.asStateFlow()

    fun loadBlogs() {
        viewModelScope.launch {
            _blogState.update { it.copy(isLoading = true, error = null) }
            when (val result = blogRepo.getNews()) {
                is ApiResult.Success -> {
                    _blogState.update {
                        it.copy(isLoading = false, blogs = result.data, error = null)
                    }
                }
                is ApiResult.Error -> {
                    _blogState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }
}
