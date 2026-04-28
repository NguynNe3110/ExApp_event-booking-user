package com.uzuu.customer.domain.model

data class ForgotPassword(
    val email: String,
    val otp: String,
    val newPassword: String
)
