package com.uzuu.customer.feature.middle.checkout

import com.uzuu.customer.domain.model.CartItem
import com.uzuu.customer.domain.model.Voucher
import java.util.Locale
import kotlin.math.min

data class CheckoutUiState(
    val isLoading: Boolean = false,
    val items: List<CartItem> = emptyList(),
    val selectedPayment: String = "MOMO",
    val selectedVoucher: Voucher? = null,
    val selectedEventId: Long? = null,
    val selectedEventName: String? = null,
    val selectedOrganizerName: String? = null
) {
    val ticketCount: Int
        get() = items.sumOf { it.quantity }

    val subtotal: Double
        get() = items.sumOf { it.subtotal }

    val discountAmount: Double
        get() {
            val voucher = selectedVoucher ?: return 0.0
            val minOrder = voucher.minOrderAmount ?: 0.0
            if (subtotal < minOrder) return 0.0

            val rawDiscount = if (voucher.discountType.uppercase(Locale.US).contains("PERCENT")) {
                subtotal * voucher.amount / 100.0
            } else {
                voucher.amount
            }
            val cap = voucher.maxDiscount?.takeIf { it > 0 } ?: rawDiscount
            return min(rawDiscount, cap).coerceAtMost(subtotal)
        }

    val payableAmount: Double
        get() = (subtotal - discountAmount).coerceAtLeast(0.0)
}
