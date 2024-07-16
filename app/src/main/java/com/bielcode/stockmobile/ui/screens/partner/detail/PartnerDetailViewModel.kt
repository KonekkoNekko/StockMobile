package com.bielcode.stockmobile.ui.screens.partner.detail

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartnerDetailViewModel(private val repository: Repository) : ViewModel() {
    private val _partnerDetails = MutableStateFlow<Partner?>(null)
    val partnerDetails: StateFlow<Partner?> = _partnerDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

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
            } else {
                Log.d("PartnerDetailViewModel", "Partner details not available for ID: $partnerId")
            }
        }
    }

    fun deleteContact(partnerName: String, contactName: String) {
        viewModelScope.launch {
            repository.deleteContact(partnerName, contactName)
            fetchPartnerDetails(partnerName) // Refresh the partner details after deletion
        }
    }
}