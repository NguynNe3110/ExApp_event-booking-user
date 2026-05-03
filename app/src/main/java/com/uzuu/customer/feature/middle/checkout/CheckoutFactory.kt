package com.uzuu.customer.feature.middle.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.CartRepository
import com.uzuu.customer.domain.repository.EventRepository
import com.uzuu.customer.domain.repository.OrderRepository
import com.uzuu.customer.domain.repository.VoucherRepository

class CheckoutFactory(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val eventRepo: EventRepository,
    private val voucherRepo: VoucherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckoutViewModel(cartRepo, orderRepo, eventRepo, voucherRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
