package com.bielcode.stockmobile.ui.screens.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.preferences.Account
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: Repository) : ViewModel() {

    private val _registerSuccess = MutableStateFlow<Boolean>(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError

    fun register(email: String, password: String, account: Account) {
        viewModelScope.launch {
            try {
                repository.register(email, password, account)
                _registerSuccess.value = true
            } catch (e: Exception) {
                _registerError.value = e.message
            }
        }
    }
}
