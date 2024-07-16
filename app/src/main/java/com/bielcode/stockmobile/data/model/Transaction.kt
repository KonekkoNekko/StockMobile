package com.bielcode.stockmobile.data.model

data class Transaction(
    val transactionAddress: String = "",
    val transactionCode: String = "",
    val transactionContact: Map<String, String> = emptyMap(),
    val transactionCoordination: String = "",
    val transactionDate: String = "",
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

