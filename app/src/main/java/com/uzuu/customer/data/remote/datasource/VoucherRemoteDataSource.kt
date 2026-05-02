package com.uzuu.customer.data.remote.datasource

import com.uzuu.customer.data.remote.api.VoucherApi

class VoucherRemoteDataSource(private val voucherApi: VoucherApi) {
    suspend fun getVouchers(page: Int = 1, size: Int = 50) =
        voucherApi.getVouchers(page.coerceAtLeast(1), size)

    suspend fun getVouchersByEvent(eventId: Long) =
        voucherApi.getVouchersByEvent(eventId)
}
