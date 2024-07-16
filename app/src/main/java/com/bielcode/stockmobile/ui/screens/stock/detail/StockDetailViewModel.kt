package com.bielcode.stockmobile.ui.screens.stock.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.preferences.Account
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _productDetails = MutableStateFlow<Stock?>(null)
    val productDetails: StateFlow<Stock?> = _productDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    private val _isCorrectRole = MutableStateFlow(false)
    val isCorrectRole: StateFlow<Boolean> = _isCorrectRole

    init {
        getCurrentRole()
    }
    fun fetchProductDetails(catalog: String) {
        Log.d("StockDetailViewModel", "Fetching product details for catalog: $catalog")
        viewModelScope.launch {
            val details = repository.getProductDetailsByCatalog(catalog)
            Log.d("StockDetailViewModel", "Fetched product details: $details")
            _productDetails.value = details

            if (details != null) {
                details.productPhotoUrl?.let { photoUrl ->
                    val imageUrl = repository.getImageUrl(photoUrl)
                    Log.d("StockDetailViewModel", "Fetched image URL: $imageUrl")
                    _imageUrl.value = imageUrl
                } ?: run {
                    Log.d("StockDetailViewModel", "Product photo URL is null")
                }
            } else {
                Log.d("StockDetailViewModel", "Fetched details are null for catalog: $catalog")
            }
        }
    }

    fun getCurrentRole() {
        viewModelScope.launch {
            repository.getSession().collect { account ->
                _isCorrectRole.value = account?.role == "Gudang"
            }
        }
    }
}

