package com.uzuu.customer.feature.middle.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.uzuu.customer.R
import com.uzuu.customer.databinding.FragmentBlogDetailBinding
import com.uzuu.customer.feature.MainActivity
import com.uzuu.customer.feature.middle.home.HomeBottomSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlogDetailFragment : Fragment() {

    private var _binding: FragmentBlogDetailBinding? = null
    private val binding get() = _binding!!

    private val args: BlogDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindData()
    }

    private fun bindData() {
        val title = args.title
        val content = args.content
        val thumbnail = args.thumbnail
        val author = args.authorName
        val published = args.publishedAt

        binding.tvTitle.text = title
        binding.tvContent.text = content
        binding.tvMeta.text = "${author.orEmpty()} • ${published.orEmpty()}"

        Glide.with(this)
            .load(thumbnail)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .centerCrop()
            .into(binding.imgThumbnail)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
