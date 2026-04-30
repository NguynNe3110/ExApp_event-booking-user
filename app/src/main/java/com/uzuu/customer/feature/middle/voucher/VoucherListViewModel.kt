package com.uzuu.customer.feature.middle.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoucherListViewModel(
    private val voucherRepo: VoucherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VoucherListUiState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<VoucherListUiEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun loadVouchers() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val r = voucherRepo.getVouchers()) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            vouchers = r.data.data.filter { voucher -> voucher.quantity > 0 }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(VoucherListUiEvent.Toast(r.message))
                }
            }
        }
    }
}
