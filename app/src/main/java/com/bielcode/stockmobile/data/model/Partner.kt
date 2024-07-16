package com.bielcode.stockmobile.data.model

import com.google.firebase.firestore.GeoPoint

data class Contact(
    val contactName: String = "",
    val contactPhone: String = "",
    val contactPosition: String = ""
)

data class PartnerType(
    val isClient: Boolean = false,
    val isConsign: Boolean = false
)

data class Partner(
    val partnerAddress: String = "",
    val partnerCategory: String = "",
    val partnerContacts: List<Contact> = emptyList(),
    val partnerCoordinate: GeoPoint? = null,
    val partnerName: String = "",
    val partnerPhone: String = "",
    val partnerPhotoUrl: String = "",
    val partnerType: PartnerType = PartnerType()
)
