package com.uzuu.customer.feature.middle.checkout

sealed class CheckoutUiEvent {
    data class Toast(val message: String) : CheckoutUiEvent()
    object CheckoutSuccess : CheckoutUiEvent()
}
