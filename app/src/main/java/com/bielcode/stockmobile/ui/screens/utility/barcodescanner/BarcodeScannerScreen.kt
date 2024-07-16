package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun BarcodeScannerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scannerViewModel: ScannerViewModel = viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val scanResult by scannerViewModel.scanResult.collectAsState()

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
                        navController.navigate("stockInput/$it/defaultSize")
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
}
