package com.bielcode.stockmobile.data.model

data class StockInput(
    val inputs: Map<String, Input>,
    val productCatalog: String,
    val productName: String,
    val productPrice: Int
)

data class Input(
    val inputQty: Int,
    val totalRestOfStock: Int,
    val transactionCode: String,
    val transactionDate: String,
    val transactionDestination: String
)