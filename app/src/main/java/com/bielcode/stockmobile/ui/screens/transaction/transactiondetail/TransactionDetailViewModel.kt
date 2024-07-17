package com.bielcode.stockmobile.ui.screens.transaction.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _transactionDetail = MutableStateFlow<Transaction?>(null)
    val transactionDetail: StateFlow<Transaction?> = _transactionDetail

    fun fetchTransactionDetail(transactionCode: String) {
        viewModelScope.launch {
            val transaction = repository.getTransactionByCode(transactionCode)
            _transactionDetail.value = transaction
        }
    }
}