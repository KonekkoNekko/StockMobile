package com.bielcode.stockmobile.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: Repository): ViewModel() {
    private val _isCorrectRole = MutableStateFlow(false)
    val isCorrectRole: StateFlow<Boolean> = _isCorrectRole

    init {
        getCurrentRole()
    }

    fun getCurrentRole() {
        viewModelScope.launch {
            repository.getSession().collect { account ->
                _isCorrectRole.value = account?.role == "Marketing"
            }
        }
    }
}