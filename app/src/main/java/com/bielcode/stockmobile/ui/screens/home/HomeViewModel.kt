package com.bielcode.stockmobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository): ViewModel(){
    private val _totalCurrentStock = MutableStateFlow(0)
    val totalCurrentStock: StateFlow<Int> = _totalCurrentStock

    init {
        fetchTotalCurrentStock()
    }

    fun fetchTotalCurrentStock() {
        viewModelScope.launch {
            _totalCurrentStock.value = repository.getTotalCurrentStock()
        }
    }

    fun logout(){
        viewModelScope.launch {
            repository.logout()
        }
    }
}