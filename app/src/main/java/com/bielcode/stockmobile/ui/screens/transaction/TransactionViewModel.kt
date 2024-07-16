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

    private fun fetchTransactions() {
        viewModelScope.launch {
            val transactions = repository.getTransactions()
            _transactionList.value = transactions.filter { it.transactionType != "Masuk" }
        }
    }

    fun filterByType(type: String): List<Transaction> {
        return _transactionList.value.filter { it.transactionType == type }
    }

    fun getStatus(transaction: Transaction): String {
        return if (transaction.transactionItems.values.all { it.isChecked }) {
            "Sudah Diantar"
        } else {
            "Siap Diantar"
        }
    }
}
