package com.bielcode.stockmobile.ui.screens.partner.contactentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.model.Contact
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactEntryViewModel(private val repository: Repository) : ViewModel() {

    private val _contactName = MutableStateFlow("")
    val contactName: StateFlow<String> = _contactName

    private val _contactPosition = MutableStateFlow("")
    val contactPosition: StateFlow<String> = _contactPosition

    private val _contactPhone = MutableStateFlow("")
    val contactPhone: StateFlow<String> = _contactPhone

    fun setContactDetails(name: String, position: String, phone: String) {
        _contactName.value = name
        _contactPosition.value = position
        _contactPhone.value = phone
    }

    fun saveContact(partnerName: String) {
        val contact = Contact(
            contactName = contactName.value,
            contactPosition = contactPosition.value,
            contactPhone = contactPhone.value
        )
        viewModelScope.launch {
            repository.addOrUpdateContact(partnerName, contact)
        }
    }

    fun deleteContact(partnerName: String) {
        viewModelScope.launch {
            repository.deleteContact(partnerName, contactName.value)
        }
    }
}

