package com.uzuu.customer.feature.middle.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.uzuu.customer.databinding.FragmentCartBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.adapter.CartItemAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    val binding get() = _binding!!

    private lateinit var cartAdapter: CartItemAdapter

    private val viewModel: CartViewModel by viewModels {
        val cartRepo  = (requireActivity() as MainActivity).container.cartRepo
        val eventRepo = (requireActivity() as MainActivity).container.eventRepo
        CartFactory(cartRepo, eventRepo)
    }

    private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        setupButtons()
        observeState()
        observeEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCart()
    }

    private fun setupRecycler() {
        cartAdapter = CartItemAdapter(
            onCheckedChange = { itemId, checked ->
                viewModel.toggleItemSelection(itemId)
            },
            onQuantityChange = { itemId, qty -> viewModel.updateItemQuantity(itemId, qty) }
        )
        binding.recyclerCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
            setHasFixedSize(false)
        }
    }

    private fun setupButtons() {
        binding.btnClearCart.setOnClickListener { viewModel.onClearCart() }
        binding.btnCheckout.setOnClickListener { openCheckoutAll() }
        binding.btnCheckoutSelected.setOnClickListener { openCheckoutSelected() }
        binding.btnDeleteSelected.setOnClickListener { viewModel.deleteSelectedItems() }

        binding.checkboxSelectAll.setOnCheckedChangeListener { _, _ ->
            viewModel.toggleSelectAll()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    val isEmpty = state.items.isEmpty() && !state.isLoading
                    binding.tvEmpty.visibility         = if (isEmpty) View.VISIBLE else View.GONE
                    binding.recyclerCart.visibility    = if (!isEmpty) View.VISIBLE else View.GONE
                    binding.cardCheckout.visibility    = if (!isEmpty) View.VISIBLE else View.GONE
                    binding.toolbarSelection.visibility = if (!isEmpty) View.VISIBLE else View.GONE

                    cartAdapter.selectedIds = state.selectedItemIds
                    cartAdapter.unavailableIds = state.unavailableItemIds
                    cartAdapter.submitList(state.items)

                    binding.checkboxSelectAll.setOnCheckedChangeListener(null)
                    binding.checkboxSelectAll.isChecked = state.isAllSelected
                    binding.checkboxSelectAll.setOnCheckedChangeListener { _, _ ->
                        viewModel.toggleSelectAll()
                    }

                    val totalToShow = if (state.hasSelection) state.selectedTotal else state.totalAmount
                    val label = if (state.hasSelection)
                        "Đã chọn (${state.selectedItemIds.size}):"
                    else
                        "Tổng cộng:"
                    binding.tvTotalLabel.text = label
                    binding.tvTotal.text = "${fmt.format(totalToShow.toLong())}đ"

                    binding.btnCheckoutSelected.isEnabled = state.hasSelection
                    binding.btnDeleteSelected.isEnabled   = state.hasSelection
                }
            }
        }
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartEvent.collect { event ->
                    when (event) {
                        is CartUiEvent.Toast ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        is CartUiEvent.CartCleared,
                        is CartUiEvent.ItemDeleted -> { /* state đã cập nhật trong VM */ }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCheckoutAll() {
        val state = viewModel.cartState.value
        if (state.items.isEmpty()) {
            Toast.makeText(context, "Gio hang dang trong", Toast.LENGTH_SHORT).show()
            return
        }
        if (state.unavailableItemIds.isNotEmpty()) {
            Toast.makeText(context, "Co ve khong con mo ban, vui long xoa khoi gio", Toast.LENGTH_SHORT).show()
            return
        }
        findNavController().navigate(R.id.checkoutFragment, bundleOf("itemIds" to longArrayOf()))
    }

    private fun openCheckoutSelected() {
        val state = viewModel.cartState.value
        if (!state.hasSelection) {
            Toast.makeText(context, "Chua chon muc nao", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedIds = state.selectedItemIds.toLongArray()
        if (selectedIds.any { it in state.unavailableItemIds }) {
            Toast.makeText(context, "Muc da chon co ve khong con mo ban", Toast.LENGTH_SHORT).show()
            return
        }
        findNavController().navigate(R.id.checkoutFragment, bundleOf("itemIds" to selectedIds))
    }
}
