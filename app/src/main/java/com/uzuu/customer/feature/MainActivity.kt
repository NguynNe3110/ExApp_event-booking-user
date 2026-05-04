package com.uzuu.customer.feature

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.uzuu.customer.R
import com.uzuu.customer.core.di.AppContainer
import com.uzuu.customer.data.session.SessionManager
import com.uzuu.customer.databinding.ActivityMainBinding
import android.net.Uri
import android.content.Intent
import com.uzuu.customer.core.constants.PaymentConstants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        container = AppContainer(applicationContext)

        handleDeepLink(intent)

        // observe global session events (e.g., session expired)
        lifecycleScope.launch {
            SessionManager.sessionEvents().collect { ev ->
                when (ev) {
                    com.uzuu.customer.data.session.SessionManager.SessionEvent.LoggedOut -> {
                        Toast.makeText(this@MainActivity, "Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show()
                        val navHostFragment =
                            supportFragmentManager.findFragmentById(R.id.root_nav_host) as NavHostFragment
                        navHostFragment.navController.navigate(
                            R.id.auth_graph, null,
                            NavOptions.Builder().setPopUpTo(R.id.root_graph, true).build()
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val uri: Uri = intent.data ?: return

        // Check if this is a payment status deep link
        if (uri.scheme != PaymentConstants.PAYMENT_DEEP_LINK_SCHEME ||
            uri.host != PaymentConstants.PAYMENT_DEEP_LINK_HOST) {
            return
        }

        // Extract payment parameters from deep link
        // Format: customer://payment-status?orderCode=<orderCode>&status=<status>
        val orderCode = uri.getQueryParameter(PaymentConstants.PARAM_ORDER_CODE).orEmpty()
        val status = uri.getQueryParameter(PaymentConstants.PARAM_STATUS).orEmpty()

        val message = when (status.lowercase()) {
            PaymentConstants.STATUS_SUCCESS,
            PaymentConstants.STATUS_PAID -> "Thanh toán thành công"
            PaymentConstants.STATUS_CANCEL,
            PaymentConstants.STATUS_CANCELED -> "Đã hủy thanh toán"
            else -> "Đã quay lại ứng dụng"
        }

        if (orderCode.isNotBlank()) {
            Toast.makeText(this, "$message: #$orderCode", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}