package com.bielcode.stockmobile.ui.screens.utility

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDateString(dateString: String): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString) ?: return dateString
    return outputFormat.format(date)
}
