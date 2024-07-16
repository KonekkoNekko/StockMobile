package com.bielcode.stockmobile.ui.screens.stock

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel(private val repository: Repository) : ViewModel() {

    private val _stocks = MutableStateFlow<List<Stock>>(emptyList())
    val stocks: StateFlow<List<Stock>> = _stocks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _imageUrls = MutableStateFlow<Map<String, Uri>>(emptyMap())
    val imageUrls: StateFlow<Map<String, Uri>> = _imageUrls

    init {
        fetchStocks()
    }

    fun fetchStocks() {
        viewModelScope.launch {
            _isLoading.value = true
            val stockList = repository.getStocks()
            _stocks.value = stockList
            _isLoading.value = false

            // Fetch image URLs
            stockList.forEach { stock ->
                viewModelScope.launch {
                    val imageUrl = repository.getImageUrl(stock.productPhotoUrl)
                    if (imageUrl != null) {
                        _imageUrls.value += (stock.productCatalog to imageUrl)
                    }
                }
            }
        }
    }

    fun setLoading(loading: Boolean) {
        viewModelScope.launch {
            _isLoading.value = loading
            if (loading) {
                kotlinx.coroutines.delay(500) // Simulate delay for loading indicator
                _isLoading.value = false
            }
        }
    }
}
