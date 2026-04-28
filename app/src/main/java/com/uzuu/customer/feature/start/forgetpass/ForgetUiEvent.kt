package com.uzuu.customer.feature.start.forgetpass

sealed class ForgetUiEvent {
    data class Toast(val message: String) : ForgetUiEvent()
    
    object navigateLogin : ForgetUiEvent()
    
    object Loading : ForgetUiEvent()
    object Success : ForgetUiEvent()
    object Error : ForgetUiEvent()
}
