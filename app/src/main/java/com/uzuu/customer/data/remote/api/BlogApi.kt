package com.uzuu.customer.data.remote.api

import com.uzuu.customer.data.remote.dto.BaseResponseDto
import com.uzuu.customer.data.remote.dto.response.BlogEventResponseDto
import retrofit2.http.GET

interface BlogApi {
    @GET("blog/news")
    suspend fun getNews(): BaseResponseDto<List<BlogEventResponseDto>>
}
