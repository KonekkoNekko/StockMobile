package com.bielcode.stockmobile.ui.screens.transaction.gudang.transactiondeliveryconfirm

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionDeliveryConfirmViewModel(private val repository: Repository): ViewModel() {
    private val _transactionDetail = MutableStateFlow<Transaction?>(null)
    val transactionDetail: StateFlow<Transaction?> = _transactionDetail

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    fun fetchTransactionDetail(transactionCode: String) {
        viewModelScope.launch {
            val transaction = repository.getTransactionByCode(transactionCode)
            _transactionDetail.value = transaction

            transaction?.transactionDocumentationUrl?.let {
                val imageUrl = repository.getImageUrl(it)
                _imageUrl.value = imageUrl
            }
        }
    }

    fun updateTransactionDocumentationUrl(transactionCode: String) {
        viewModelScope.launch {

            val transaction = repository.getTransaction(transactionCode)
            transaction?.let {
                val updatedTransaction = it.copy(
                    transactionDocumentationUrl = "transactions/${transactionCode}_delivered.jpg"
                )
                repository.updateTransaction(transactionCode, updatedTransaction)
                Log.d("ViewModel", "Transaction documentation URL updated successfully: ${updatedTransaction.transactionDocumentationUrl}")
            }

        }
    }

    fun setImageUrl(uri: Uri) {
        _imageUrl.value = uri
    }

    fun decreaseStock(catalog: String, size: String, quantity: Int) {
        viewModelScope.launch {
            repository.decreaseStock(catalog, size, quantity)
        }
    }

}