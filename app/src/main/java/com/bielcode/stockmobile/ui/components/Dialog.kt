package com.bielcode.stockmobile.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DialogWindow(
    titleText: String,
    contentText: String,
    confirmText: String,
    dismissText: String,
    clickConfirm: () -> Unit,
    clickDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { clickDismiss() },
        dismissButton = {
            TextButton(onClick = clickDismiss) {
                Text(text = dismissText)
            }
        },
        confirmButton = {
            TextButton(onClick = clickConfirm) {
                Text(text = confirmText)
            }
        },
        icon = { Icons.Default.Info },
        text = { Text(text = contentText, textAlign = TextAlign.Left) },
        title = { Text(text = titleText) })
}

@Preview
@Composable
fun DialogPreview() {
    DialogWindow(
        titleText = "Input Stok Masuk",
        contentText = "Anda akan menambahkan data stok terbaru ke dalam database!\nPastikan produk yang dipindai sesuai dengan katalog yang bersangkutan!",
        confirmText = "Lanjutkan",
        dismissText = "Batalkan",
        clickConfirm = {},
        clickDismiss = {})
}