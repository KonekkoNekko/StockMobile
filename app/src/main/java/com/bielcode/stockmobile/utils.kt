package com.bielcode.stockmobile

import java.text.NumberFormat
import java.util.Locale

fun formatPriceWithDots(price: Int): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.GERMANY)
    return numberFormat.format(price)
}