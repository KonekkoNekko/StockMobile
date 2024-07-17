package com.bielcode.stockmobile.ui.screens.partner.entry

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.Coordinate
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.model.PartnerType
import com.bielcode.stockmobile.data.repository.Repository
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartnerEntryViewModel(private val repository: Repository) : ViewModel() {
    private val _partnerDetails = MutableStateFlow<Partner?>(null)
    val partnerDetails: StateFlow<Partner?> = _partnerDetails

    private val _imageUrl = MutableStateFlow<Uri?>(null)
    val imageUrl: StateFlow<Uri?> = _imageUrl

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    val name = MutableStateFlow("")
    val address = MutableStateFlow("")
    val phone = MutableStateFlow("")
    val category = MutableStateFlow("")
    val partnerLabels = MutableStateFlow(mapOf<String, Boolean>())
    val coordinate = MutableStateFlow<Coordinate?>(null)

    fun fetchPartnerDetails(partnerId: String) {
        viewModelScope.launch {
            val details = repository.getPartnerDetailsByName(partnerId)
            _partnerDetails.value = details

            details?.partnerPhotoUrl?.let { photoUrl ->
                val imageUrl = repository.getImageUrl(photoUrl)
                _imageUrl.value = imageUrl
            }

            details?.let { partner ->
                val labels = mapOf(
                    "isClient" to partner.partnerType.isClient,
                    "isConsign" to partner.partnerType.isConsign
                )
                partnerLabels.value = labels
                Log.d("PartnerEntryViewModel", "Fetched partner details: $details")
                Log.d("PartnerEntryViewModel", "Partner labels: $labels")
            }
        }
    }

    fun setImageUrl(uri: Uri) {
        _imageUrl.value = uri
    }

    fun savePartner() {
        viewModelScope.launch {
            _isSaving.value = true

            val existingContacts = partnerDetails.value?.partnerContacts ?: emptyList()
            Log.d("PartnerEntryViewModel", "Existing contacts: $existingContacts")

            val partner = Partner(
                partnerName = name.value,
                partnerAddress = address.value,
                partnerPhone = phone.value,
                partnerCategory = category.value,
                partnerCoordinate = coordinate.value?.let { GeoPoint(it.lat, it.lng) },
                partnerType = PartnerType(
                    isClient = partnerLabels.value["isClient"] ?: false,
                    isConsign = partnerLabels.value["isConsign"] ?: false
                ),
                partnerPhotoUrl = "partners/${name.value}.jpg",
                partnerContacts = existingContacts
            )

            Log.d("PartnerEntryViewModel", "Saving partner: $partner")
            repository.savePartner(partner)
            _isSaving.value = false
        }
    }

    fun deletePartner(partnerId: String) {
        viewModelScope.launch {
            _isSaving.value = true
            repository.deletePartner(partnerId)
            _isSaving.value = false
        }
    }

    fun fetchLocation() {
        viewModelScope.launch {
            repository.getLocation().collect { (address, coordinate) ->
                if (address.isNotEmpty() && coordinate.isNotEmpty()) {
                    val coords = coordinate.split(",")
                    if (coords.size == 2) {
                        val lat = coords[0].toDoubleOrNull() ?: 0.0
                        val lng = coords[1].toDoubleOrNull() ?: 0.0
                        this@PartnerEntryViewModel.address.value = address
                        this@PartnerEntryViewModel.coordinate.value = Coordinate("Selected Location", lat, lng)
                        Log.d("PartnerEntryViewModel", "Fetched location: address = $address, coordinate = $lat,$lng")
                        repository.clearLocation()
                    }
                }
            }
        }
    }
}
