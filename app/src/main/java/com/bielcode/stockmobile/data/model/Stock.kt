package com.bielcode.stockmobile.data.model

data class Stock(
    val productCatalog: String = "",
    val productDetails: Map<String, ProductDetail> = emptyMap(),
    val productName: String = "",
    val productPhotoUrl: String = "",
    val productPrice: Int = 0,
    val productTag: String = ""
)

data class ProductDetail(
    val stockConsign: Int = 0,
    val stockCurrent: Int = 0,
    val stockInitial: Int = 0,
    val stockSold: Int = 0
)
