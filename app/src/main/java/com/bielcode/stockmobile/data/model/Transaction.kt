package com.bielcode.stockmobile.data.model

import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Transaction(
    val transactionCode: String = "",
    val transactionAddress: String = "",
    val transactionContact: Map<String, Any> = emptyMap(),
    val transactionCoordination: GeoPoint? = null,
    val transactionDate: Date? = null,
    val transactionDestination: String = "",
    val transactionDocumentUrl: String = "",
    val transactionDocumentationUrl: String = "",
    val transactionItems: Map<String, TransactionItem> = emptyMap(),
    val transactionPhone: String = "",
    val transactionType: String = ""
)

data class TransactionItem(
    val isChecked: Boolean = false,
    val itemCatalog: String = "",
    val itemName: String = "",
    val itemQty: Int = 0,
    val itemSize: String = ""
)
