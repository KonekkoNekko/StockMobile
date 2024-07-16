package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScannerViewModel(private val repository: Repository) : ViewModel() {

    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult

    private val _productDetails = MutableStateFlow<Stock?>(null)
    val productDetails: StateFlow<Stock?> = _productDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    fun setScanResult(result: String) {
        _scanResult.value = result
        fetchProductDetails(result)
    }

    private fun fetchProductDetails(catalog: String) {
        viewModelScope.launch {
            val details = repository.getProductDetailsByCatalog(catalog)
            _productDetails.value = details
            details?.productPhotoUrl?.let {
                val imageUrl = repository.getImageUrl(it)
                _imageUrl.value = imageUrl
            }
        }
    }
}
