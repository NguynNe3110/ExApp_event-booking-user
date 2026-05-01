package com.uzuu.customer.feature.middle.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.uzuu.customer.databinding.FragmentBlogBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.ui.adapter.BlogAdapter
import kotlinx.coroutines.launch

class BlogFragment : Fragment() {

    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    private lateinit var blogAdapter: BlogAdapter

    private val viewModel: BlogViewModel by viewModels {
        val blogRepo = (requireActivity() as MainActivity).container.blogRepo
        BlogFactory(blogRepo)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecycler()
        observeState()
        viewModel.loadBlogs()
    }

    private fun setupRecycler() {
        blogAdapter = BlogAdapter()
        binding.recyclerBlog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = blogAdapter
            setHasFixedSize(false)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.blogState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    blogAdapter.submitList(state.blogs)

                    val message = state.error ?: if (state.blogs.isEmpty() && !state.isLoading) {
                        "Chưa có bài viết"
                    } else {
                        ""
                    }
                    binding.tvEmpty.text = message
                    binding.tvEmpty.visibility = if (message.isBlank()) View.GONE else View.VISIBLE
                    binding.recyclerBlog.visibility =
                        if (state.blogs.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
