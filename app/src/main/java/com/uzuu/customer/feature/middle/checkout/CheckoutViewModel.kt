package com.uzuu.customer.feature.middle.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.domain.model.CartItem
import com.uzuu.customer.domain.model.Event
import com.uzuu.customer.domain.model.Voucher
import com.uzuu.customer.domain.repository.CartRepository
import com.uzuu.customer.domain.repository.EventRepository
import com.uzuu.customer.domain.repository.OrderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartRepo: CartRepository,
    private val orderRepo: OrderRepository,
    private val eventRepo: EventRepository
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
                    val voucherContext = resolveVoucherContext(items)
                    _state.update {
                        it.copy(
                            isLoading = false,
                            items = items,
                            selectedEventId = voucherContext?.eventId,
                            selectedEventName = voucherContext?.eventName,
                            selectedOrganizerName = voucherContext?.organizerName
                        )
                    }
                    if (items.isEmpty()) {
                        _event.emit(CheckoutUiEvent.Toast("Không có vé để thanh toán"))
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
            viewModelScope.launch { _event.emit(CheckoutUiEvent.Toast("Không có vé để thanh toán")) }
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
                    _event.emit(CheckoutUiEvent.Toast("Đặt hàng thành công"))
                    _event.emit(CheckoutUiEvent.CheckoutSuccess)
                }
                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.emit(CheckoutUiEvent.Toast(result.message))
                }
            }
        }
    }

    private suspend fun resolveVoucherContext(items: List<CartItem>): VoucherContext? {
        if (items.isEmpty()) return null

        val cachedEvents = runCatching { eventRepo.getCachedEvents() }.getOrDefault(emptyList())
        val eventsByName = mutableMapOf<String, List<Event>>()

        val resolvedEvent = items.firstNotNullOfOrNull { item ->
            val events = eventsByName.getOrPut(item.eventName) {
                runCatching {
                    eventRepo.searchEvents(
                        page = 1,
                        search = item.eventName,
                        province = null,
                        minPrice = null,
                        maxPrice = null
                    ).data
                }.getOrElse {
                    cachedEvents.filter { event ->
                        event.name.equals(item.eventName, ignoreCase = true) ||
                            event.name.contains(item.eventName, ignoreCase = true)
                    }
                }
            }

            events.firstOrNull { event ->
                event.ticketTypes.any { it.id == item.ticketTypeId }
            }
        }

        return resolvedEvent?.let { event ->
            VoucherContext(
                eventId = event.id,
                eventName = event.name,
                organizerName = event.organizerName
            )
        } ?: items.firstOrNull()?.let { first ->
            VoucherContext(
                eventId = null,
                eventName = first.eventName,
                organizerName = null
            )
        }
    }

    private data class VoucherContext(
        val eventId: Long?,
        val eventName: String?,
        val organizerName: String?
    )
}
