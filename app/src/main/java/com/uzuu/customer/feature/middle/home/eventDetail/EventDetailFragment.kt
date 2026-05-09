package com.uzuu.customer.feature.middle.home.eventDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.feature.MainActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.uzuu.customer.R
import com.uzuu.customer.databinding.FragmentEventDetailBinding
import com.uzuu.customer.domain.model.Event
import com.uzuu.customer.ui.adapter.CategoryTicketAdapter
import com.uzuu.customer.ui.adapter.EventAdapter

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    val binding get() = _binding!!

    private val args: EventDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val event = args.event
        bindData(event)
    }

    private fun bindData(event: Event) {
        val imageUrl = EventAdapter.Companion.fixImageUrl(event.imageUrls.firstOrNull())
        Glide.with(binding.imgEventDetail)
            .load(imageUrl)
            .centerCrop()
            .into(binding.imgEventDetail)

        binding.txtEventName.text       = event.name
        binding.txtCategory.text        = event.categoryName
        binding.txtLocation.text        = event.location
        binding.txtOrganizer.text       = event.organizerName?.takeIf { it.isNotBlank() } ?: "Khong ro"
        binding.txtStartTime.text       = "Bắt đầu: ${event.startTime ?: "Chưa xác định"}"
        binding.txtEndTime.text         = "Kết thúc: ${event.endTime ?: "Chưa xác định"}"
        binding.txtSaleStart.text       = "Mở bán: ${event.saleStartDate ?: "Chưa xác định"}"
        binding.txtSaleEnd.text         = "Kết thúc bán: ${event.saleEndDate ?: "Chưa xác định"}"
        binding.txtDescription.text     = event.description?.ifBlank { "Chưa có mô tả." } ?: "Chưa có mô tả."

        val (statusLabel, statusColor) = when (event.status) {
            "PENDING"    -> "● Sắp diễn ra"  to requireContext().getColor(R.color.event_upcoming)
            "ON_SALE"    -> "● Đang bán vé"  to requireContext().getColor(R.color.event_on_sale)
            "ONGOING"    -> "● Đang diễn ra" to requireContext().getColor(R.color.event_ongoing)
            "COMPLETED"  -> "● Đã kết thúc"  to requireContext().getColor(R.color.event_completed)
            else         -> "● ${event.status}" to requireContext().getColor(R.color.blue_text_secondary)
        }
        binding.txtStatus.text      = statusLabel
        binding.txtStatus.setTextColor(statusColor)

        val ticketAdapter = CategoryTicketAdapter()
        binding.recyclerTicketTypes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ticketAdapter
            setHasFixedSize(false)
        }
        ticketAdapter.submitList(event.ticketTypes)

        binding.btnBuyNow.setOnClickListener {
            val ticketTypeId = event.ticketTypes.firstOrNull()?.id
            if (ticketTypeId == null) {
                findNavController().popBackStack()
                return@setOnClickListener
            }

            val cartRepo = (requireActivity() as MainActivity).container.cartRepo
            lifecycleScope.launch {
                when (val r = cartRepo.addToCart(ticketTypeId, 1)) {
                    is ApiResult.Success -> {
                        val ids = r.data.items.mapNotNull { it.id }.toLongArray()
                        findNavController().navigate(R.id.checkoutFragment, bundleOf("itemIds" to ids))
                    }
                    is ApiResult.Error -> {
                        android.widget.Toast.makeText(requireContext(), r.message, android.widget.Toast.LENGTH_SHORT).show()
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