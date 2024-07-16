package com.bielcode.stockmobile.ui.screens.utility

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDateString(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val date = inputFormat.parse(dateString)
    return if (date != null) {
        outputFormat.format(date)
    } else {
        dateString
    }
}