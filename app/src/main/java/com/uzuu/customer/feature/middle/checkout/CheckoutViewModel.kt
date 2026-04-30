package com.uzuu.customer.feature.middle.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.domain.model.Voucher
import com.uzuu.customer.domain.repository.CartRepository
import com.uzuu.customer.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutUiState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<CheckoutUiEvent>(extraBufferCapacity = 2)
    val event = _event.asSharedFlow()

    private var selectedIds: List<Long> = emptyList()

    fun loadCheckoutItems(ids: LongArray) {
        selectedIds = ids.toList()
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = cartRepo.getCart()) {
                is ApiResult.Success -> {
                    val items = if (selectedIds.isEmpty()) {
                        r.data.items
                    } else {
                        r.data.items.filter { it.id in selectedIds }
                    }
                    _state.update { it.copy(isLoading = false, items = items) }
                    if (items.isEmpty()) {
                        _event.emit(CheckoutUiEvent.Toast("Khong co ve de thanh toan"))
                    }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CheckoutUiEvent.Toast(r.message))
                }
            }
        }
    }

    fun selectPayment(method: String) {
        _state.update { it.copy(selectedPayment = method) }
    }

    fun selectVoucher(voucher: Voucher?) {
        _state.update { it.copy(selectedVoucher = voucher) }
    }

    fun checkout() {
        val current = _state.value
        if (current.items.isEmpty()) {
            viewModelScope.launch { _event.emit(CheckoutUiEvent.Toast("Khong co ve de thanh toan")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val voucherCode = current.selectedVoucher?.code
            val result = if (selectedIds.isEmpty()) {
                orderRepo.checkout(current.selectedPayment, voucherCode)
            } else {
                orderRepo.checkoutSelected(current.selectedPayment, selectedIds, voucherCode)
            }
            when (result) {
                is ApiResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CheckoutUiEvent.Toast("Dat hang thanh cong"))
                    _event.emit(CheckoutUiEvent.CheckoutSuccess)
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CheckoutUiEvent.Toast(result.message))
                }
            }
        }
    }
}
