package com.uzuu.customer.feature.middle.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzuu.customer.R
import com.uzuu.customer.core.qrcode.QrCodeGenerator
import com.uzuu.customer.databinding.BottomsheetPaymentResultBinding
import com.uzuu.customer.domain.model.Order
import java.text.NumberFormat
import java.util.Locale

class PaymentResultBottomSheet(
    private val order: Order,
    private val onDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetPaymentResultBinding? = null
    private val binding get() = _binding!!

    private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetPaymentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupButtons()
    }

    private fun setupUI() {
        binding.tvOrderId.text = "Đơn hàng: #${order.id}"
        binding.tvAmount.text = "${fmt.format(order.totalAmount.toLong())}đ"
        binding.tvPaymentMethod.text = when (order.paymentMethod) {
            "VIETQR" -> "VietQR"
            "PAYOS" -> "PayOS"
            else -> order.paymentMethod
        }

        when (order.paymentMethod) {
            "VIETQR" -> displayVietQrCode()
            else -> {
                binding.imgQrCode.visibility = View.GONE
                binding.tvQrHint.visibility = View.GONE
            }
        }

        when (order.paymentStatus) {
            "PAID" -> {
                binding.tvPaymentStatus.text = "✓ Đã thanh toán"
                binding.tvPaymentStatus.setTextColor(requireContext().getColor(R.color.event_completed))
            }
            "PENDING" -> {
                binding.tvPaymentStatus.text = "⏳ Chờ thanh toán"
                binding.tvPaymentStatus.setTextColor(requireContext().getColor(R.color.event_on_sale))
            }
            else -> {
                binding.tvPaymentStatus.text = "✗ Thất bại"
                binding.tvPaymentStatus.setTextColor(requireContext().getColor(R.color.event_ongoing))
            }
        }
    }

    private fun displayVietQrCode() {
        val qrString = order.paymentUrl
        if (qrString.isNullOrBlank()) {
            binding.imgQrCode.visibility = View.GONE
            binding.tvQrHint.visibility = View.GONE
            return
        }

        try {
            val qrBitmap = QrCodeGenerator.generateQrCode(qrString)
            if (qrBitmap != null) {
                binding.imgQrCode.setImageBitmap(qrBitmap)
                binding.tvQrHint.text = "Quét mã QR bằng ứng dụng ngân hàng hoặc ứng dụng thanh toán"
                binding.imgQrCode.visibility = View.VISIBLE
                binding.tvQrHint.visibility = View.VISIBLE
            } else {
                binding.imgQrCode.visibility = View.GONE
                binding.tvQrHint.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Lỗi sinh mã QR: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.imgQrCode.visibility = View.GONE
            binding.tvQrHint.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener {
            dismiss()
            onDismiss()
        }
        binding.btnBack.setOnClickListener {
            dismiss()
            onDismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            order: Order,
            onDismiss: () -> Unit
        ) {
            PaymentResultBottomSheet(order, onDismiss).show(fragmentManager, "payment_result")
        }
    }
}
