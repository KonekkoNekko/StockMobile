package com.bielcode.stockmobile.ui.screens.utility.mapspicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log

class MapsPickerViewModel(private val repository: Repository) : ViewModel() {
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> get() = _selectedLocation

    private val _selectedAddress = MutableStateFlow("")
    val selectedAddress: StateFlow<String> get() = _selectedAddress

    fun updateLocation(latLng: LatLng, address: String) {
        _selectedLocation.value = latLng
        _selectedAddress.value = address
        Log.d("MapsPickerViewModel", "Updated location: latLng = $latLng, address = $address")
    }

    fun saveLocation() {
        val latLng = _selectedLocation.value
        val address = _selectedAddress.value

        if (latLng != null) {
            viewModelScope.launch {
                repository.saveLocation(address, "${latLng.latitude},${latLng.longitude}")
                Log.d("MapsPickerViewModel", "Saved location: address = $address, coordinate = ${latLng.latitude},${latLng.longitude}")
            }
        }
    }
}
