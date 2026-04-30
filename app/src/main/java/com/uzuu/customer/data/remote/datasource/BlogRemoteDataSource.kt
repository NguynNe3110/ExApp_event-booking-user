package com.uzuu.customer.data.remote.datasource

import com.uzuu.customer.data.remote.api.BlogApi
import com.uzuu.customer.data.remote.dto.BaseResponseDto
import com.uzuu.customer.data.remote.dto.response.BlogEventResponseDto

class BlogRemoteDataSource(
    private val blogApi: BlogApi
) {
    suspend fun getNews(): BaseResponseDto<List<BlogEventResponseDto>> {
        return blogApi.getNews()
    }
}
