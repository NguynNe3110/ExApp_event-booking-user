package com.uzuu.customer.feature.middle.home.eventExtra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uzuu.customer.R
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.databinding.FragmentCategoryEventsBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.feature.middle.home.HomeBottomSheet
import com.uzuu.customer.ui.adapter.EventAdapter
import kotlinx.coroutines.launch

class CategoryEventsFragment : Fragment() {

    private var _binding: FragmentCategoryEventsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: EventAdapter

    private var categoryId: Long = -1
    private var categoryName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getLong("categoryId", -1)
            categoryName = it.getString("categoryName", "") ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCategoryEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = EventAdapter { event ->
            showBottomSheet(event)
        }
        binding.recyclerCategoryEvents.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CategoryEventsFragment.adapter
            setHasFixedSize(true)
        }

        loadEvents()
    }

    private fun loadEvents() {
        binding.progress.visibility = View.VISIBLE
        val eventRepo = (requireActivity() as MainActivity).container.eventRepo
        lifecycleScope.launch {
            try {
                val result = eventRepo.searchEvents(page = 1, search = null, province = null, minPrice = null, maxPrice = null, categoryId = categoryId)
                binding.progress.visibility = View.GONE
                adapter.submitList(result.data)
                binding.tvEmpty.visibility = if (result.data.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            }
        }
    }

    private fun showBottomSheet(event: com.uzuu.customer.domain.model.Event) {
        if (parentFragmentManager.findFragmentByTag("event_bottom_sheet") != null) return

        HomeBottomSheet(
            event = event,
            onViewDetail = {
                findNavController().navigate(
                    R.id.eventDetailFragment,
                    bundleOf("event" to event)
                )
            },
            onAddToCart = { ticketTypeId, qty ->
                val cartRepo = (requireActivity() as MainActivity).container.cartRepo
                val result = cartRepo.addToCart(ticketTypeId, qty)
                if (result is ApiResult.Error) {
                    throw Exception(result.message)
                }
            }
        ).show(parentFragmentManager, "event_bottom_sheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}