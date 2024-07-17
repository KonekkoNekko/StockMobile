package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class TransactionStockInputViewModel(private val repository: Repository) : ViewModel() {
    private val _transactionItems = MutableStateFlow<List<TransactionItem>>(emptyList())
    val transactionItems: StateFlow<List<TransactionItem>> = _transactionItems

    private val _productDetails = MutableStateFlow<Stock?>(null)
    val productDetails: StateFlow<Stock?> = _productDetails

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    fun fetchProductDetails(catalog: String) {
        viewModelScope.launch {
            val details = repository.getProductDetailsByCatalog(catalog)
            _productDetails.value = details
            details?.productPhotoUrl?.let { photoUrl ->
                _imageUrl.value = repository.getImageUrl(photoUrl)
            }
            Log.d("TransactionStockInputVM", "Fetched product details: $details")
        }
    }

    fun addTransactionItem(item: TransactionItem) {
        Log.d("TransactionStockInputVM", "Adding item: $item")
        _isSaving.value = true
        _transactionItems.value += item
        saveTransactionToPreferences()
        _isSaving.value = false
        Log.d("TransactionStockInputVM", "Current transaction items: ${_transactionItems.value}")
    }

    private fun saveTransactionToPreferences() {
        viewModelScope.launch {
            val transaction = repository.getTransactionFromPreferences().firstOrNull() ?: Transaction(transactionDate = Date())
            val updatedTransaction = transaction.copy(
                transactionItems = transaction.transactionItems.toMutableMap().apply {
                    put(_transactionItems.value.last().itemCatalog, _transactionItems.value.last())
                }
            )
            repository.saveTransactionToPreferences(updatedTransaction)
        }
    }
}
