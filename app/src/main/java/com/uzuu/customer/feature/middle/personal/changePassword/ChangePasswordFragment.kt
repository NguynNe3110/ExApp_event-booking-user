package com.uzuu.customer.feature.middle.personal.changePassword

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
import com.uzuu.customer.databinding.FragmentChangePasswordBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.dialog.showConfirmDialog
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {

    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChangePasswordViewModel by viewModels {
        ChangePasswordFactory((requireActivity() as MainActivity).container.authRepo,
            (requireActivity() as MainActivity).container.userRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnSubmit.setOnClickListener {
            val old = binding.edtOldPassword.text.toString().trim()
            val nw = binding.edtNewPassword.text.toString().trim()
            val conf = binding.edtConfirmPassword.text.toString().trim()

            showConfirmDialog(
                title = "Xác nhận đổi mật khẩu",
                message = "Bạn có chắc chắn muốn đổi mật khẩu?",
                positiveText = "Đổi",
                negativeText = "Hủy",
                onPositive = {
                    viewModel.changePassword(old, nw, conf)
                }
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
                        binding.btnSubmit.isEnabled = !loading
                    }
                }

                launch {
                    viewModel.events.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        if (msg.contains("thành công")) {
                            findNavController().popBackStack()
                        }
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
