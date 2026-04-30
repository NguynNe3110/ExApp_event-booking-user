package com.uzuu.customer.data.repository

import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.core.result.safeApiCall
import com.uzuu.customer.data.mapper.toDomain
import com.uzuu.customer.data.remote.datasource.BlogRemoteDataSource
import com.uzuu.customer.domain.model.BlogEvent
import com.uzuu.customer.domain.repository.BlogRepository

class BlogRepositoryImpl(
    private val remote: BlogRemoteDataSource
) : BlogRepository {

    private fun isOk(code: Int) = code == 200 || code == 0 || code == 1000

    override suspend fun getNews(): ApiResult<List<BlogEvent>> =
        safeApiCall {
            val response = remote.getNews()
            if (isOk(response.code)) {
                response.result.map { it.toDomain() }
            } else {
                throw Exception(response.message.ifBlank { "Khong tai duoc blog" })
            }
        }
}
