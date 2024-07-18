package com.bielcode.stockmobile.ui.screens.partner.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.ProductItemSizeOwnStts
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.model.Transaction
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartnerDetailViewModel(private val repository: Repository) : ViewModel() {
    private val _partnerDetails = MutableStateFlow<Partner?>(null)
    val partnerDetails: StateFlow<Partner?> = _partnerDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _products = MutableStateFlow<List<ProductItemSizeOwnStts>>(emptyList())
    val products: StateFlow<List<ProductItemSizeOwnStts>> = _products

    fun fetchPartnerDetails(partnerId: String) {
        Log.d("PartnerDetailViewModel", "Fetching partner details for ID: $partnerId")
        viewModelScope.launch {
            val details = repository.getPartnerDetailsByName(partnerId)
            _partnerDetails.value = details

            if (details != null) {
                details.partnerPhotoUrl?.let { photoUrl ->
                    val imageUrl = repository.getImageUrl(photoUrl)
                    Log.d("PartnerDetailViewModel", "Image URL: $imageUrl")
                    _imageUrl.value = imageUrl
                } ?: run {
                    Log.d("PartnerDetailViewModel", "No image URL available.")
                }

                fetchTransactionsByPartner(details.partnerName)
                fetchProductsByPartner(details.partnerName)
            } else {
                Log.d("PartnerDetailViewModel", "Partner details not available for ID: $partnerId")
            }
        }
    }

    private fun fetchTransactionsByPartner(partnerName: String) {
        viewModelScope.launch {
            _transactions.value = repository.getTransactionsByPartner(partnerName)
        }
    }

    private fun fetchProductsByPartner(partnerName: String) {
        viewModelScope.launch {
            _products.value = repository.getProductsByPartner(partnerName)
        }
    }

    fun deleteContact(partnerName: String, contactName: String) {
        viewModelScope.launch {
            repository.deleteContact(partnerName, contactName)
            fetchPartnerDetails(partnerName) // Refresh the partner details after deletion
        }
    }
}