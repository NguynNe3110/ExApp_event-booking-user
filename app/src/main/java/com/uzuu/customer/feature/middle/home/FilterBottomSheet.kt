package com.uzuu.customer.feature.middle.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uzuu.customer.databinding.BottomsheetFilterBinding

class FilterBottomSheet(
    private val onApplyFilters: (city: String, minPrice: Double?, maxPrice: Double?) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        setupButtons()
    }

    private fun setupSpinner() {
        val provinces = ProvinceData.VIETNAM_PROVINCES
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnApply.setOnClickListener {
            val selected = binding.spinnerCity.selectedItem?.toString().orEmpty()
            val city = if (selected == "Tất cả tỉnh/thành") "" else selected
            val minPrice = binding.edtMinPrice.text.toString().toDoubleOrNull()
            val maxPrice = binding.edtMaxPrice.text.toString().toDoubleOrNull()

            onApplyFilters(city, minPrice, maxPrice)
            dismiss()
        }

        binding.btnClear.setOnClickListener {
            binding.spinnerCity.setSelection(0)
            binding.edtMinPrice.setText("")
            binding.edtMaxPrice.setText("")
            onApplyFilters("", null, null)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
