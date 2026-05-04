package com.uzuu.customer.feature.start.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.uzuu.customer.R
import com.uzuu.customer.data.session.SessionManager
import com.uzuu.customer.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            delay(2000) // Show splash for 2 seconds

            // Check if user is logged in
            if (SessionManager.getToken() != null) {
                // Navigate to home/main
                findNavController().navigate(
                    R.id.action_splash_to_home,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.auth_graph, true)
                        .build()
                )
            } else {
                // Navigate to login
                findNavController().navigate(
                    R.id.action_splash_to_login,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
