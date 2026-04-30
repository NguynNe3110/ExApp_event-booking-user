package com.uzuu.customer.data.remote.datasource

import com.uzuu.customer.data.remote.api.OrderApi
import com.uzuu.customer.data.remote.dto.BaseResponseDto
import com.uzuu.customer.data.remote.dto.response.OrderResponseDto
import com.uzuu.customer.data.remote.dto.response.PageResponse

class OrderRemoteDataSource(private val orderApi: OrderApi) {

    suspend fun checkout(
        paymentMethod: String,
        voucherCode: String?
    ): BaseResponseDto<OrderResponseDto> =
        orderApi.checkout(paymentMethod, voucherCode)

    suspend fun checkoutSelected(
        paymentMethod: String,
        itemIds: List<Long>,
        voucherCode: String?
    ): BaseResponseDto<OrderResponseDto> =
        orderApi.checkoutSelected(paymentMethod = paymentMethod, voucherCode = voucherCode, itemIds = itemIds)

    suspend fun getMyOrders(page: Int): BaseResponseDto<PageResponse<OrderResponseDto>> =
        orderApi.getMyOrders(page, 20)
}
