@file:OptIn(ExperimentalMaterial3Api::class)

package com.bielcode.stockmobile.ui.screens.utility.mapspicker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

@Composable
fun MapsPickerScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var loading by remember { mutableStateOf(false) }
    val viewModel: MapsPickerViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(context)
        )
    )
    val surabaya = LatLng(-7.2575, 112.7521)
    val markerState = rememberMarkerState(position = surabaya)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(surabaya, 12f)
    }

    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    LaunchedEffect(markerState.position) {
        val newLatLngValue = markerState.position

        var address: Address? = null
        coroutineScope.launch {
            loading = true
            address = getAddressFromLocation(
                context, newLatLngValue.latitude, newLatLngValue.longitude
            )
        }.invokeOnCompletion {
            loading = false
            viewModel.updateLocation(newLatLngValue, address?.getAddressLine(0) ?: newLatLngValue.toString())
            markerState.position = newLatLngValue
        }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            },
            title = { Text(text = "Pilih Lokasi Mitra") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                titleContentColor = MaterialTheme.colorScheme.primary,
                actionIconContentColor = MaterialTheme.colorScheme.tertiary,
            ),
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        GoogleMap(
                            modifier = Modifier
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp)),
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
                            onMapClick = { latLngValue ->
                                coroutineScope.launch {
                                    loading = true
                                    val address = getAddressFromLocation(
                                        context, latLngValue.latitude, latLngValue.longitude
                                    )
                                    loading = false
                                    viewModel.updateLocation(latLngValue, address?.getAddressLine(0) ?: latLngValue.toString())
                                    Log.d("MapsPickerScreen", "Map clicked: latLng = $latLngValue, address = ${viewModel.selectedAddress.value}")
                                }
                            }
                        ) {
                            Marker(
                                state = markerState,
                                title = selectedAddress.ifBlank { null },
                                snippet = selectedLocation?.let { "${it.latitude}, ${it.longitude}" },
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED),
                                draggable = true
                            )
                        }
                    }
                    if (loading) CircularProgressIndicator(
                        modifier = Modifier.padding(8.dp)
                    )
                }

                if (selectedAddress.isNotEmpty() && selectedLocation != null) {
                    SelectedLocationCard(
                        onConfirmRequest = {
                            viewModel.saveLocation()
                            navController.popBackStack()
                        },
                        selectedAddress = selectedAddress,
                        latLng = selectedLocation!!
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        // Handle the myLocationButton click
        coroutineScope.launch {
            getCurrentLocation(fusedLocationClient)?.let { location ->
                val currentLatLng = LatLng(location.latitude, location.longitude)
                cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLatLng, 15f)
                markerState.position = currentLatLng

                val address = getAddressFromLocation(context, currentLatLng.latitude, currentLatLng.longitude)
                viewModel.updateLocation(currentLatLng, address?.getAddressLine(0) ?: currentLatLng.toString())
                Log.d("MapsPickerScreen", "Current location: latLng = $currentLatLng, address = ${viewModel.selectedAddress.value}")
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

suspend fun getAddressFromLocation(
    context: Context,
    latitude: Double,
    longitude: Double
): Address? {
    return withContext(Dispatchers.IO) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                return@withContext addresses[0]
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
