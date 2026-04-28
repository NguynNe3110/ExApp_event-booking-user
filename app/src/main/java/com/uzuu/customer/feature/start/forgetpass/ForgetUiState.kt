package com.uzuu.customer.feature.start.forgetpass

data class ForgetUiState(
    val email: String = "",
    val otp: String = "",
    val isLoading: Boolean = false,
    val isOtpSent: Boolean = false
)
