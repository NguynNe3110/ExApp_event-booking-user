package com.uzuu.customer.feature.start.forgetpass

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.AuthRepository

class ForgetFactory(
    private val authRepo: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetViewModel::class.java)) {
            return ForgetViewModel(authRepo) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}
