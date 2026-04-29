package com.uzuu.customer.feature.start.forgetpass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uzuu.customer.databinding.FragmentForgetOtpBinding

class ForgetOtpFragment : Fragment() {

    private var _binding: FragmentForgetOtpBinding? = null
    val binding get() = _binding!!

    private val args: ForgetOtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.txtEmailHint.text = maskEmail(args.email)

        binding.btnVerifyOtp.setOnClickListener {
            val otp = binding.inputOtp.text.toString().trim()
            if (otp.isBlank()) {
                Toast.makeText(context, "Vui lòng nhập mã xác thực", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val action = ForgetOtpFragmentDirections
                .actionForgetOtpToForgetReset(otp)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun maskEmail(email: String): String {
        val atIndex = email.indexOf('@')
        if (atIndex <= 1) return email
        val localPart = email.substring(0, atIndex)
        val domain = email.substring(atIndex)
        val masked = localPart.first() + "*".repeat((localPart.length - 1).coerceAtLeast(1)) + domain
        return masked
    }
}
