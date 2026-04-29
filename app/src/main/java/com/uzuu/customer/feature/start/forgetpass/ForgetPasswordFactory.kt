package com.uzuu.customer.feature.start.forgetpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.AuthRepository

class ForgetPasswordFactory(
    private val authRepo: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ForgetPasswordViewModel(authRepo) as T
    }
}
