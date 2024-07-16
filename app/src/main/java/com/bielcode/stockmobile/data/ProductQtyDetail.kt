package com.bielcode.stockmobile.data

data class ProductQtyDetail(
    val size: String,
    val qtyInitial: Int,
    val qtyInWarehouse: Int,
    val qtySold: Int,
    val qtyConsigned: Int
)
