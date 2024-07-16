package com.bielcode.stockmobile.ui.screens.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginSuccess = MutableStateFlow<Boolean>(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                if (email.isNotEmpty() && password.isNotEmpty()){
                    repository.login(email, password)
                    _loginSuccess.value = true
                    _loginError.value = null // Reset error state on success
                }
            } catch (e: Exception) {
                _loginError.value = e.message
                _loginSuccess.value = false // Reset success state on error
            }
        }
    }


}
