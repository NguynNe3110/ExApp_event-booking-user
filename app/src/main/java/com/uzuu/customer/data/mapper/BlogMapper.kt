package com.uzuu.customer.data.mapper

import com.uzuu.customer.data.remote.dto.response.BlogPostResponseDto
import com.uzuu.customer.domain.model.BlogEvent

fun BlogPostResponseDto.toDomain(): BlogEvent {
    return BlogEvent(
        id = id,
        name = title.orEmpty(),
        location = authorName.orEmpty(),
        province = summary.orEmpty(),
        startTime = publishedAt,
        endTime = createdAt,
        saleStartDate = null,
        saleEndDate = null,
        descriptionStatus = status,
        imageUrl = thumbnail
    )
}
