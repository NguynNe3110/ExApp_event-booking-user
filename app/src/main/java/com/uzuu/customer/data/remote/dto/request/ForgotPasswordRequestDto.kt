package com.uzuu.customer.data.remote.dto.request

data class ForgotPasswordRequestDto(
    val email: String,
    val otp: String,
    val newPassword: String
)
