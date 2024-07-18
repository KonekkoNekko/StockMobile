package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.DialogWindow
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BarcodeScannerScreen(
    navController: NavHostController,
    catalog: String,
    itemSize: String = "",
    itemQty: Int = 0,
    transactionCode: String = ""
) {
    val context = LocalContext.current
    val scannerViewModel: ScannerViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val scanResult by scannerViewModel.scanResult.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_ALL_FORMATS
    ).build()

    val scanner = GmsBarcodeScanning.getClient(context, options)
    val barcodeResult = MutableStateFlow<String?>(null)

    suspend fun startScan() {
        try {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcode.displayValue?.let {
                        Log.d("BarcodeScanner", "Scanned barcode value: $it")
                        if (it == catalog) {
                            // Navigasi ke CheckTransactionStockInputScreen jika barcode cocok
                            if (itemSize.isNotEmpty() && itemQty != 0 && transactionCode.isNotEmpty()) {
                                navController.navigate("checkTransactionStockInput/${catalog}/${itemSize}/${itemQty}/${transactionCode}")
                            } else {
                                navController.navigate("stockInput/$it/$itemSize")
                            }

                        } else {
                            // Tampilkan dialog jika barcode tidak cocok
                            showDialog = true
                        }
                    }
                }
                .addOnCanceledListener {
                    barcodeResult.value = "Cancelled"
                    navController.navigateUp()
                }
                .addOnFailureListener { e ->
                    barcodeResult.value = "Failed"
                    Log.e("BarcodeScanner", "Barcode scan failed: ${e.message}")
                    navController.navigateUp()
                }
        } catch (e: Exception) {
            Log.e("BarcodeScanner", "Barcode scan failed: ${e.message}")
        }
    }

    LaunchedEffect(Unit) {
        startScan()
    }

    if (showDialog) {
        DialogWindow(
            titleText = "Barcode tidak cocok",
            contentText = "Barcode tidak cocok, silakan coba lagi.",
            confirmText = "OK",
            dismissText = "",
            clickConfirm = {
                showDialog = false
                navController.popBackStack()
            },
            clickDismiss = {
                showDialog = false
                navController.popBackStack()
            }
        )
    }
}
