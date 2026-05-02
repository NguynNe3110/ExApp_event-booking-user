package com.uzuu.customer.feature.middle.checkout

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uzuu.customer.R
import com.uzuu.customer.databinding.FragmentCheckoutBinding
import com.uzuu.customer.domain.model.Voucher
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.adapter.CheckoutTicketAdapter
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModels {
        val container = (requireActivity() as MainActivity).container
        CheckoutFactory(container.cartRepo, container.orderRepo, container.eventRepo)
    }

    private val ticketAdapter = CheckoutTicketAdapter()
    private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupPayment()
        setupButtons()
        observeVoucherResult()
        observeState()
        observeEvent()

        val ids = arguments?.getLongArray("itemIds") ?: longArrayOf()
        viewModel.loadCheckoutItems(ids)
    }

    private fun setupRecycler() {
        binding.recyclerTickets.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ticketAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupPayment() {
        val methods = listOf("MOMO", "VNPAY")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, methods)
        binding.dropdownPayment.setAdapter(adapter)
        binding.dropdownPayment.setText(methods.first(), false)
        binding.dropdownPayment.setOnItemClickListener { _, _, position, _ ->
            viewModel.selectPayment(methods[position])
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.rowVoucher.setOnClickListener {
            val state = viewModel.state.value
            findNavController().navigate(
                R.id.voucherListFragment,
                bundleOf(
                    "eventId" to state.selectedEventId,
                    "eventName" to state.selectedEventName,
                    "organizerName" to state.selectedOrganizerName
                )
            )
        }
        binding.btnCheckout.setOnClickListener { viewModel.checkout() }
    }

    private fun observeVoucherResult() {
        val handle = findNavController().currentBackStackEntry?.savedStateHandle ?: return
        handle.getLiveData<Voucher>("selectedVoucher").observe(viewLifecycleOwner) { voucher ->
            viewModel.selectVoucher(voucher)
            handle.remove<Voucher>("selectedVoucher")
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    ticketAdapter.submitList(state.items)
                    binding.tvTicketCount.text = state.ticketCount.toString()
                    binding.tvSubtotal.text = money(state.subtotal)
                    binding.tvDiscount.text = "-${money(state.discountAmount)}"
                    binding.tvPayable.text = money(state.payableAmount)
                    binding.tvVoucherCode.text = state.selectedVoucher?.code ?: "Chọn voucher"
                    binding.tvVoucherOrganizer.text = when {
                        state.selectedVoucher != null -> {
                            val eventName = state.selectedVoucher.eventName?.takeIf { it.isNotBlank() }
                                ?: state.selectedEventName?.takeIf { it.isNotBlank() }
                                ?: "Khong ro"
                            val organizer = state.selectedVoucher.creatorName?.takeIf { it.isNotBlank() }
                                ?: state.selectedOrganizerName?.takeIf { it.isNotBlank() }
                                ?: "Khong ro"
                            "Sự kiện: $eventName | Tổ chức: $organizer"
                        }
                        state.selectedEventName != null || state.selectedOrganizerName != null -> {
                            val eventName = state.selectedEventName?.takeIf { it.isNotBlank() } ?: "Khong ro"
                            val organizer = state.selectedOrganizerName?.takeIf { it.isNotBlank() } ?: "Khong ro"
                            "Áp dụng cho: $eventName | Tổ chức: $organizer"
                        }
                        else -> "Chưa áp dụng mã giảm giá"
                    }
                    binding.dropdownPayment.setText(state.selectedPayment, false)
                    binding.btnCheckout.isEnabled = !state.isLoading && state.items.isNotEmpty()
                }
            }
        }
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is CheckoutUiEvent.Toast ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        is CheckoutUiEvent.CheckoutSuccess ->
                            findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun money(value: Double): String = "${fmt.format(value.toLong())}d"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
