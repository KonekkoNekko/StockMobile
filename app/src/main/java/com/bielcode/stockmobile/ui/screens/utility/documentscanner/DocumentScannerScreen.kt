package com.bielcode.stockmobile.ui.screens.utility.documentscanner

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScannerScreen(
    navController: NavController,
    transactionCode: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val documentScannerViewModel: DocumentScannerViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(context)
        )
    )

    // Ensure the saved URIs are loaded
    LaunchedEffect(Unit) {
        documentScannerViewModel.loadSavedUris(context)
    }

    val imageUris by documentScannerViewModel.imageUris.collectAsState()

    // Mengambil Activity dari context
    val activity = context as? Activity

    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setGalleryImportAllowed(true)
        .setPageLimit(5)
        .setResultFormats(
            GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
            GmsDocumentScannerOptions.RESULT_FORMAT_PDF
        )
        .build()

    val scanner = GmsDocumentScanning.getClient(options)

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(data)
                if (scanResult != null) {
                    documentScannerViewModel.handleScanResult(context, scanResult, transactionCode)
                } else {
                    Log.e("DocumentScannerScreen", "Scan result is null")
                }
            }
        } else {
            Log.e("DocumentScannerScreen", "Scan was unsuccessful")
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Pindai Dokumen", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "BackArrow")
            }
        })
    }) {
        Surface(
            modifier = Modifier
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (imageUris.isNotEmpty()) {
                    imageUris.forEach { uri ->
                        Log.d("DocumentScannerScreen", "Displaying image URI: $uri")
                        AsyncImage(
                            model = uri,
                            contentDescription = "Scanned Document",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    Text(text = "No scanned documents available")
                }

                Button(
                    onClick = {
                        activity?.let {
                            scanner.getStartScanIntent(it)
                                .addOnSuccessListener { intentSender ->
                                    scannerLauncher.launch(
                                        IntentSenderRequest.Builder(intentSender).build()
                                    )
                                }
                                .addOnFailureListener {
                                    Log.e("DocumentScannerScreen", "Error launching scanner: $it")
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = "Scan Document",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Scan Dokumen")
                }
            }
        }
    }
}
