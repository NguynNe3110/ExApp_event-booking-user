package com.uzuu.customer.feature.middle.personal.personalInfo

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
import com.uzuu.customer.databinding.FragmentPersonalInfoBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.feature.middle.personal.PersonalFactory
import com.uzuu.customer.feature.middle.personal.PersonalUiEvent
import com.uzuu.customer.feature.middle.personal.PersonalViewModel
import com.uzuu.customer.ui.dialog.showConfirmDialog
import kotlinx.coroutines.launch

class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    val binding get() = _binding!!

    private val viewModel: PersonalViewModel by viewModels {
        PersonalFactory((requireActivity() as MainActivity).container.userRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.init()
        setupSaveButton()
        observeState()
        observeEvent()

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupSaveButton() {
        binding.btnRegister.setOnClickListener {
            val email    = binding.edtEmail.text.toString().trim()
            val fullName = binding.edtFullName.text.toString().trim()
            val phone    = binding.edtPhoneNumber.text.toString().trim()
            val address  = binding.edtAddress.text.toString().trim()

            if (email.isBlank() || fullName.isBlank() || phone.isBlank() || address.isBlank()) {
                Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmDialog(
                title = "Xác nhận cập nhật",
                message = "Bạn có chắc chắn muốn lưu những thay đổi này?",
                positiveText = "Lưu",
                negativeText = "Hủy",
                onPositive = {
                    viewModel.updateInfo(email, fullName, phone, address)
                }
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->

                    if (!state.isLoading) {
                        binding.edtUsername.setText(state.username)
                        binding.edtEmail.setText(state.email)
                        binding.edtFullName.setText(state.fullName)
                        binding.edtPhoneNumber.setText(state.phone)
                        binding.edtAddress.setText(state.address)
                    }

                    val editable = !state.isLoading
                    binding.edtEmail.isEnabled       = editable
                    binding.edtFullName.isEnabled    = editable
                    binding.edtPhoneNumber.isEnabled = editable
                    binding.edtAddress.isEnabled     = editable
                    binding.btnRegister.isEnabled    = editable
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                }
            }
        }
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is PersonalUiEvent.Toast -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            if (event.message.contains("thành công")) {
                                findNavController().popBackStack()
                            }
                        }
                        else -> {}
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