package com.bielcode.stockmobile.ui.screens.partner

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Partner
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PartnerViewModel(private val repository: Repository): ViewModel() {
    private val _partners = MutableStateFlow<List<Partner>>(emptyList())
    val partners: StateFlow<List<Partner>> = _partners

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isCorrectRole = MutableStateFlow(false)
    val isCorrectRole: StateFlow<Boolean> = _isCorrectRole

    private val _imageUrls = MutableStateFlow<Map<String, Uri>>(emptyMap())
    val imageUrls: StateFlow<Map<String, Uri>> = _imageUrls

    init {
        fetchPartners()
        getCurrentRole()
    }

    fun fetchPartners() {
        viewModelScope.launch {
            _isLoading.value = true
            val partnerList = repository.getPartners()
            _partners.value = partnerList
            _isLoading.value = false

            partnerList.forEach { partner ->
                Log.d("PartnerViewModel", "Partner: ${partner.partnerName}, isClient: ${partner.partnerType.isClient}, isConsign: ${partner.partnerType.isConsign}")
                viewModelScope.launch {
                    val imageUrl = repository.getImageUrl(partner.partnerPhotoUrl)
                    if (imageUrl != null) {
                        _imageUrls.value += (partner.partnerName to imageUrl)
                    }
                }
            }
        }
    }

    fun setLoading(loading: Boolean) {
        viewModelScope.launch {
            _isLoading.value = loading
            if (loading) {
                kotlinx.coroutines.delay(500)
                _isLoading.value = false
            }
        }
    }

    fun getCurrentRole() {
        viewModelScope.launch {
            repository.getSession().collect { account ->
                _isCorrectRole.value = account?.role == "Marketing"
            }
        }
    }
}
