package com.bielcode.stockmobile.data

data class TransactionSimplified(
    val code: String,
    val type: String,
    val date: String,
    val qty: Int,
)
