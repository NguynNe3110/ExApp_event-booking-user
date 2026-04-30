package com.uzuu.customer.data.remote.dto.response

data class BlogEventResponseDto(
    val id: Long,
    val name: String?,
    val location: String?,
    val province: String?,
    val startTime: String?,
    val endTime: String?,
    val saleStartDate: String?,
    val saleEndDate: String?,
    val descriptionStatus: String?,
    val imageUrl: String?
)
