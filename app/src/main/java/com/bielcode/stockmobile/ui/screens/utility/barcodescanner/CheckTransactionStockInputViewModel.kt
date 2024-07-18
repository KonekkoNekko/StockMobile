package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CheckTransactionStockInputViewModel(private val repository: Repository) : ViewModel() {
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
            Log.d("CheckTransactionStockInputVM", "Fetched product details: $details")
        }
    }

    fun updateTransactionItem(
        catalog: String, size: String, qty: Int, transactionCode: String
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            val transaction = repository.getTransaction(transactionCode)
            transaction?.let {
                val updatedItems = it.transactionItems.toMutableMap()
                val updatedItem = updatedItems[catalog]?.copy(itemQty = qty, isChecked = true)
                if (updatedItem != null) {
                    updatedItems[catalog] = updatedItem
                }
                val updatedTransaction = it.copy(transactionItems = updatedItems)
                repository.updateTransaction(transactionCode, updatedTransaction)
            }
            _isSaving.value = false
        }
    }
}
