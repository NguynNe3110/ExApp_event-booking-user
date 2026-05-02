package com.uzuu.customer.feature.middle.voucher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uzuu.customer.databinding.FragmentVoucherListBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.adapter.VoucherAdapter
import kotlinx.coroutines.launch

class VoucherListFragment : Fragment() {

    private var _binding: FragmentVoucherListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VoucherListViewModel by viewModels {
        val container = (requireActivity() as MainActivity).container
        val args = arguments
        val eventId = if (args?.containsKey("eventId") == true) args.getLong("eventId") else null
        VoucherListFactory(
            container.voucherRepo,
            eventId,
            args?.getString("eventName"),
            args?.getString("organizerName")
        )
    }

    private lateinit var voucherAdapter: VoucherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoucherListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        voucherAdapter = VoucherAdapter { voucher ->
            voucherAdapter.selectedVoucherId = voucher.id
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("selectedVoucher", voucher)
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.recyclerVouchers.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = voucherAdapter
        }

        observeState()
        observeEvent()
        viewModel.loadVouchers()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.tvEmpty.visibility =
                        if (!state.isLoading && state.vouchers.isEmpty()) View.VISIBLE else View.GONE
                    voucherAdapter.submitList(state.vouchers)
                }
            }
        }
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is VoucherListUiEvent.Toast ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
