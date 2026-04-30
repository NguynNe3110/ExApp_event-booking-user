package com.uzuu.customer.data.mapper

import com.uzuu.customer.data.remote.dto.response.BlogEventResponseDto
import com.uzuu.customer.domain.model.BlogEvent

fun BlogEventResponseDto.toDomain(): BlogEvent {
    return BlogEvent(
        id = id,
        name = name.orEmpty(),
        location = location.orEmpty(),
        province = province.orEmpty(),
        startTime = startTime,
        endTime = endTime,
        saleStartDate = saleStartDate,
        saleEndDate = saleEndDate,
        descriptionStatus = descriptionStatus,
        imageUrl = imageUrl
    )
}
