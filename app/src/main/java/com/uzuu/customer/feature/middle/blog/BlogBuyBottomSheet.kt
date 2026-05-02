package com.uzuu.customer.feature.middle.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzuu.customer.databinding.BottomsheetBlogBuyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlogBuyBottomSheet(
    private val onAddToCart: suspend (ticketTypeId: Long, quantity: Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetBlogBuyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetBlogBuyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAddToCart.setOnClickListener {
            val idText = binding.edtTicketTypeId.text.toString().trim()
            val qtyText = binding.edtQuantity.text.toString().trim()
            if (idText.isEmpty() || qtyText.isEmpty()) {
                Toast.makeText(context, "Nhập ticketTypeId và số lượng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val id = try { idText.toLong() } catch (e: Exception) { -1L }
            val qty = try { qtyText.toInt() } catch (e: Exception) { 0 }
            if (id <= 0 || qty <= 0) {
                Toast.makeText(context, "Giá trị không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnAddToCart.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    onAddToCart(id, qty)
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show()
                        binding.btnAddToCart.isEnabled = true
                        dismiss()
                    }
                } catch (e: Exception) {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(context, "Lỗi khi thêm vé: ${e.message}", Toast.LENGTH_SHORT).show()
                        binding.btnAddToCart.isEnabled = true
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
