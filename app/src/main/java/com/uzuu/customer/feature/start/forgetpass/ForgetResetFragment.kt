package com.uzuu.customer.feature.start.forgetpass

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.uzuu.customer.R
import com.uzuu.customer.databinding.FragmentForgetResetBinding
import com.uzuu.customer.feature.MainActivity
import kotlinx.coroutines.launch

class ForgetResetFragment : Fragment() {

    private var _binding: FragmentForgetResetBinding? = null
    val binding get() = _binding!!

    private val args: ForgetResetFragmentArgs by navArgs()

    private val viewModel: ForgetPasswordViewModel by viewModels {
        val authRepo = (requireActivity() as MainActivity).container.authRepo
        ForgetPasswordFactory(authRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupEvent()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.btnResetPassword.isEnabled = !state.isLoading
                    binding.btnResetPassword.text =
                        if (state.isLoading) "" else "Đặt lại mật khẩu"
                    binding.inputNewPassword.isEnabled = !state.isLoading
                    binding.inputConfirmPassword.isEnabled = !state.isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is ForgetPasswordUiEvent.Toast -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        }
                        is ForgetPasswordUiEvent.NavigateToLogin -> {
                            findNavController().navigate(
                                R.id.loginFragment,
                                null,
                                NavOptions.Builder()
                                    .setPopUpTo(R.id.auth_graph, false)
                                    .build()
                            )
                        }
                        is ForgetPasswordUiEvent.NavigateToOtp -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupEvent() {
        binding.btnResetPassword.setOnClickListener {
            viewModel.resetPassword(
                otp = args.otp,
                newPassword = binding.inputNewPassword.text.toString().trim(),
                confirmPassword = binding.inputConfirmPassword.text.toString().trim()
            )
        }
    }
}
