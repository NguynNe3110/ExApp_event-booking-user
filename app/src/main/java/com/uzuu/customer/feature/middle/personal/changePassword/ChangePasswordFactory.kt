package com.uzuu.customer.feature.middle.personal.changePassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uzuu.customer.domain.repository.AuthRepository
import com.uzuu.customer.domain.repository.UserRepository

class ChangePasswordFactory(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(authRepo, userRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
