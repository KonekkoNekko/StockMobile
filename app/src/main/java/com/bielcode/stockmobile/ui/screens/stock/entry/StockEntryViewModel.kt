package com.bielcode.stockmobile.ui.screens.stock.entry

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockEntryViewModel(private val repository: Repository) : ViewModel() {

    private val _productDetails = MutableStateFlow<Stock?>(null)
    val productDetails: StateFlow<Stock?> = _productDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    val name = MutableStateFlow("")
    val catalogNumber = MutableStateFlow("")
    val price = MutableStateFlow("")
    val selectedChips = MutableStateFlow(listOf<String>())

    fun fetchProductDetails(catalog: String) {
        viewModelScope.launch {
            val details = repository.getProductDetailsByCatalog(catalog)
            _productDetails.value = details

            details?.productPhotoUrl?.let { photoUrl ->
                val imageUrl = repository.getImageUrl(photoUrl)
                _imageUrl.value = imageUrl
            }
        }
    }

    fun saveProduct(stock: Stock) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.saveProduct(stock)
            _isSaving.value = false
        }
    }

    fun deleteProduct(catalog: String) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.deleteProduct(catalog)
            _isSaving.value = false
        }
    }

    fun setImageUrl(uri: Uri) {
        _imageUrl.value = uri
    }
}
