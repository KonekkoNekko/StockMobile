package com.bielcode.stockmobile.ui.screens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(private val repository: Repository): ViewModel() {
    private val _isCorrectRole = MutableStateFlow(false)
    val isCorrectRole: StateFlow<Boolean> = _isCorrectRole

    private val _transactionList = MutableStateFlow<List<Transaction>>(emptyList())
    val transactionList: StateFlow<List<Transaction>> = _transactionList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        getCurrentRole()
        fetchTransactions()
    }

    fun getCurrentRole() {
        viewModelScope.launch {
            repository.getSession().collect { account ->
                _isCorrectRole.value = account?.role == "Marketing"
            }
        }
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            val transactions = repository.getTransactions()
            _isLoading.value = true
            _transactionList.value = transactions.filter { it.transactionType != "Masuk" }
            _isLoading.value = false
        }
    }

    fun filterByType(type: String): List<Transaction> {
        return _transactionList.value.filter { it.transactionType == type }
    }

    fun getStatus(transaction: Transaction): String {
        return if (transaction.transactionDocumentationUrl == "") {
            "Siap Diantar"
        } else {
            "Sudah Diantar"
        }
    }
}
