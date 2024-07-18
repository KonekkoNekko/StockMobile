@file:OptIn(
    ExperimentalLayoutApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)

package com.bielcode.stockmobile.ui.screens.transaction.transactiondetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.ContactCard_Call
import com.bielcode.stockmobile.ui.components.DialogWindow
import com.bielcode.stockmobile.ui.components.ProductCard_SizeQty
import com.bielcode.stockmobile.ui.components.ProductCard_SizeQtyChk
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.screens.partner.detail.openGoogleMaps
import com.bielcode.stockmobile.ui.screens.utility.formatDateString
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionCode: String
) {
    val context = LocalContext.current
    val viewModel: TransactionDetailViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository(context))
    )

    val transaction by viewModel.transactionDetail.collectAsStateWithLifecycle()

    LaunchedEffect(transactionCode) {
        viewModel.fetchTransactionDetail(transactionCode)
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Rincian Transaksi", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        }, actions = {
            IconButton(onClick = {
                navController.navigate("transactionEntry/${transactionCode}")
            }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                )
            }
        })
    }) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            if (transaction == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Log.d("TransactionDetailScreen", "transaction: $transaction")
                transaction?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Kode Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = it.transactionCode,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Tipe Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = it.transactionType,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }


                        Column {
                            Text(
                                text = "Tanggal Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = formatDateString(it.transactionDate.toString()),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }

                        Column {
                            Text(text = "Destinasi", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = it.transactionDestination,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(text = "Alamat", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = it.transactionAddress,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Nomor Telepon",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = it.transactionPhone,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "Penerima", style = MaterialTheme.typography.titleMedium)
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val contactMap = it.transactionContact as Map<String, Any>
                                ContactCard_Call(
                                    name = contactMap["contactName"] as String,
                                    position = contactMap["contactPosition"] as String,
                                    phone = contactMap["contactPhone"] as String,
                                    onCallIcon = {
                                        val phoneNumber = contactMap["contactPhone"] as String
                                        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse(url)
                                            setPackage("com.whatsapp")
                                        }
                                        if (intent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "WhatsApp not installed.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Dokumen Terkait",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(
                                onClick = {
                                    navController.navigate("documentScanner/${it.transactionCode}?isForRead=true")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Baca")
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Daftar Bawaan",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                it.transactionItems.values.forEach { product ->
                                    ProductCard_SizeQty(
                                        name = product.itemName,
                                        size = product.itemSize,
                                        qty = product.itemQty,
                                        catalog = product.itemCatalog
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Lokasi Destinasi",
                            style = MaterialTheme.typography.titleMedium
                        )

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    it.transactionCoordination?.latitude ?: 0.0,
                                    it.transactionCoordination?.longitude ?: 0.0
                                ), 12f
                            )
                        }

                        Box(
                            modifier = Modifier.size(330.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(
                                            it.transactionCoordination?.latitude ?: 0.0,
                                            it.transactionCoordination?.longitude ?: 0.0
                                        )
                                    ),
                                    title = it.transactionDestination,
                                    tag = it.transactionDestination
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDetailScreen_Delivery(
    navController: NavController,
    transactionCode: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val viewModel: TransactionDetailViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository(context))
    )

    val transaction by viewModel.transactionDetail.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }
    var isWithinGeofence by remember { mutableStateOf(false) }
    var userLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(transactionCode) {
        viewModel.fetchTransactionDetail(transactionCode)
    }

    LaunchedEffect(Unit) {
        // Request location permission
        locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Function to check if the user is within the geofence
    fun checkGeofence(userLocation: LatLng, geofenceCenter: LatLng, radius: Double): Boolean {
        val results = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            geofenceCenter.latitude, geofenceCenter.longitude, results
        )
        return results[0] < radius
    }

    if (hasLocationPermission) {
        LaunchedEffect(Unit) {
            // Get user's current location
            coroutineScope.launch {
                while (true) {
                    getCurrentLocation(fusedLocationClient)?.let { location ->
                        userLocation = LatLng(location.latitude, location.longitude)
                        val geofenceCenter = LatLng(
                            transaction?.transactionCoordination?.latitude ?: 0.0,
                            transaction?.transactionCoordination?.longitude ?: 0.0
                        )
                        isWithinGeofence = checkGeofence(userLocation, geofenceCenter, 50.0)
                        Log.d("TransactionDetailScreen_Delivery", "User location: $userLocation, within geofence: $isWithinGeofence")
                    }
                    delay(5000) // Update location every 5 seconds
                }
            }
        }
    } else {
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Rincian Transaksi", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {
                navController.navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        })
    }) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            if (transaction == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Log.d("TransactionDetailScreen_Delivery", "transaction: $transaction")
                transaction?.let { transactionItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Kode Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = transactionItem.transactionCode,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Tipe Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = transactionItem.transactionType,
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Tanggal Transaksi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = formatDateString(transactionItem.transactionDate.toString()),
                                style = MaterialTheme.typography.headlineLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Destinasi",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = transactionItem.transactionDestination,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Alamat",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = transactionItem.transactionAddress,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(
                                text = "Nomor Telepon",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = transactionItem.transactionPhone,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Dokumen Terkait",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Button(
                                onClick = {
                                    navController.navigate("documentScanner/${transactionItem.transactionCode}?isForRead=true")
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Baca")
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Daftar Bawaan",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                transactionItem.transactionItems.values.forEach { product ->
                                    ProductCard_SizeQtyChk(
                                        name = product.itemName,
                                        size = product.itemSize,
                                        qty = product.itemQty,
                                        checked = product.isChecked,
                                        onIconClick = {
                                            // Navigasi ke barcode scanner dengan membawa item Catalog, itemSize, itemQty, dan transactionCode
                                            navController.navigate("barcodeScanner/${product.itemCatalog}/${product.itemSize}/${product.itemQty}/${transactionCode}")
                                        },
                                        catalog = product.itemCatalog
                                    )
                                }

                            }
                        }

                        Text(
                            text = "Lokasi Destinasi",
                            style = MaterialTheme.typography.titleMedium
                        )

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    transactionItem.transactionCoordination?.latitude ?: 0.0,
                                    transactionItem.transactionCoordination?.longitude ?: 0.0
                                ), 25f
                            )
                        }

                        Box(
                            modifier = Modifier.size(330.dp)
                        ) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(
                                    isBuildingEnabled = true,
                                    isMyLocationEnabled = true,
                                    isTrafficEnabled = true,
                                    isIndoorEnabled = true,
                                ),
                                uiSettings = MapUiSettings(
                                    compassEnabled = true,
                                    mapToolbarEnabled = true,
                                    indoorLevelPickerEnabled = true,
                                    myLocationButtonEnabled = true,
                                ),
                            ) {
                                val destinationLatLng = LatLng(
                                    transactionItem.transactionCoordination?.latitude ?: 0.0,
                                    transactionItem.transactionCoordination?.longitude ?: 0.0
                                )

                                Marker(
                                    state = MarkerState(
                                        position = destinationLatLng
                                    ),
                                    title = transactionItem.transactionDestination,
                                    tag = transactionItem.transactionDestination
                                )

                                Circle(
                                    center = destinationLatLng,
                                    radius = 50.0,
                                    strokeColor = Color.Red,
                                    strokeWidth = 2f
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val allUnchecked = transactionItem.transactionItems.values.all { !it.isChecked }
                                if (allUnchecked) {
                                    showDialog = true
                                } else {
                                    val name = transactionItem?.transactionDestination
                                    transactionItem?.transactionCoordination?.let { coordinate ->
                                        openGoogleMapsNavigation(
                                            context,
                                            coordinate.latitude,
                                            coordinate.longitude,
                                            name
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = "Start Delivery"
                            )
                            Text(text = "Mulai Pengiriman")
                        }

                        Button(
                            onClick = {
                                if (isWithinGeofence) {
                                    navController.navigate("${Screen.TransactionDeliveryConfirm.route}/${transactionCode}")
                                } else {
                                    showDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .align(Alignment.CenterHorizontally),
                            enabled = isWithinGeofence
                        ) {
                            Icon(
                                imageVector = Icons.Default.Checklist,
                                contentDescription = "Confirm Delivery"
                            )
                            Text(text = "Konfirmasi Pengiriman")
                        }

                        if (showDialog) {
                            DialogWindow(
                                titleText = "Validasi Pengiriman",
                                contentText = "Anda harus berada dalam radius 10 meter untuk memulai pengiriman.",
                                clickConfirm = {
                                    showDialog = false
                                    navController.popBackStack()
                                },
                                clickDismiss = {},
                                confirmText = "OK",
                                dismissText = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
suspend fun getCurrentLocation(fusedLocationClient: FusedLocationProviderClient): android.location.Location? {
    return withContext(Dispatchers.IO) {
        try {
            val locationResult = fusedLocationClient.lastLocation
            locationResult.await()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun openGoogleMapsNavigation(context: Context, latitude: Double, longitude: Double, label: String?) {
    val uri = Uri.parse("google.navigation:q=$latitude,$longitude($label)")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}