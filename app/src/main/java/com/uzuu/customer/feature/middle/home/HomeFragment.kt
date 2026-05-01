package com.uzuu.customer.feature.middle.home

import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uzuu.customer.R
import com.uzuu.customer.core.result.ApiResult
import com.uzuu.customer.data.session.SessionManager
import com.uzuu.customer.databinding.FragmentHomeBinding
import com.uzuu.customer.domain.model.Event
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.adapter.CategoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    val binding get() = _binding!!

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categorySectionAdapter: com.uzuu.customer.ui.adapter.CategorySectionAdapter

    private val viewModel: HomeViewModel by viewModels {
        val eventRepo    = (requireActivity() as MainActivity).container.eventRepo
        val categoryRepo = (requireActivity() as MainActivity).container.categoryRepo
        HomeFactory(eventRepo, categoryRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupAdapters()
        setupSearch()
        setupFilters()
        observeState()
        observeEvent()
        setupPagination()
        viewModel.init()
    }

    override fun onResume() {
        super.onResume()
        viewModel.startPolling(15_000L)  // 15 giây/lần
        val uri = SessionManager.getAvatarUri()
        if (!uri.isNullOrBlank()) {
            Glide.with(this)
                .load(Uri.parse(uri))
                .circleCrop()
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(binding.imgAvatar)
        }
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { clickedCategory ->
            viewModel.onCategorySelected(clickedCategory)
        }
        // hide category strip on the event screen per product request
        binding.txtCategoryTitle.visibility = View.GONE
        binding.recyclerCategory.visibility = View.GONE

        categorySectionAdapter = com.uzuu.customer.ui.adapter.CategorySectionAdapter(
            onEventClick = { event -> showBottomSheet(event) },
            onViewMoreClick = { categoryId, categoryName ->
                val bundle = androidx.core.os.bundleOf(
                    "categoryId" to categoryId,
                    "categoryName" to categoryName
                )
                findNavController().navigate(R.id.action_homeFragment_to_categoryEventsFragment, bundle)
            }
        )

        binding.recyclerEvent.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categorySectionAdapter
            setHasFixedSize(false)
            itemAnimator = null
        }
    }

    private fun setupSearch() {
        binding.edtSearch.addTextChangedListener { editable ->
            viewModel.onSearch(editable?.toString() ?: "")
        }
    }

    private fun setupFilters() {
        // setup city spinner
        val provinces = ProvinceData.VIETNAM_PROVINCES
        val adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, provinces)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter

        binding.spinnerCity.setSelection(0)
        binding.spinnerCity.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                val city = if (position == 0) "" else provinces[position]
                viewModel.onFiltersChanged(city = city, minPrice = null, maxPrice = null)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        binding.btnOpenFilters.setOnClickListener {
            val bottom = FilterBottomSheet { city, minPrice, maxPrice ->
                viewModel.onFiltersChanged(city = city, minPrice = minPrice, maxPrice = maxPrice)
            }
            bottom.show(parentFragmentManager, "home_filter_sheet")
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeState.collect { state ->
                    categoryAdapter.submitList(state.categories)

                    // prepare grouped list and append suggestion section if present
                    val grouped = state.groupedEvents.toMutableList()
                    if (state.suggestionEvents.isNotEmpty()) {
                        grouped.add(
                            com.uzuu.customer.feature.middle.home.CategoryWithEvents(
                                categoryId = -2,
                                categoryName = "Bạn có thể thích",
                                displayedEvents = state.suggestionEvents,
                                totalEventCount = state.suggestionEvents.size,
                                hasMoreEvents = false
                            )
                        )
                    }

                    categorySectionAdapter.submitList(grouped)
                }
            }
        }
    }

    private fun observeEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.homeEvent.collect { event ->
                    when (event) {
                        is HomeUiEvent.Toast ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                        is HomeUiEvent.navigateBack -> {
                            SessionManager.clear()
                            val rootNavController = (requireActivity() as MainActivity)
                                .supportFragmentManager
                                .findFragmentById(R.id.root_nav_host)
                                .let { it as androidx.navigation.fragment.NavHostFragment }
                                .navController
                            rootNavController.navigate(
                                R.id.auth_graph, null,
                                NavOptions.Builder().setPopUpTo(R.id.root_graph, true).build()
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupPagination() {
        binding.recyclerEvent.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val lm = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = lm.findLastVisibleItemPosition()
                val total = lm.itemCount
                if (lastVisible >= total - 3) {
                    viewModel.loadMoreEvents()
                }
            }
        })
    }

    private fun showBottomSheet(event: Event) {
        if (parentFragmentManager.findFragmentByTag("event_bottom_sheet") != null) return

        HomeBottomSheet(
            event = event,
            onAddToCart = { ticketTypeId, qty ->
                val cartRepo = (requireActivity() as MainActivity).container.cartRepo
                val result = cartRepo.addToCart(ticketTypeId, qty)
                if (result is ApiResult.Error) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    throw Exception(result.message)
                }
            }
        ).show(parentFragmentManager, "event_bottom_sheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopPolling()
        _binding = null
    }
}
