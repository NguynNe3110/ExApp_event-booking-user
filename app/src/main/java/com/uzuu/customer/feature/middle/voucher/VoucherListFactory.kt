package com.uzuu.customer.feature.middle.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.VoucherRepository

class VoucherListFactory(
    private val voucherRepo: VoucherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoucherListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VoucherListViewModel(voucherRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
