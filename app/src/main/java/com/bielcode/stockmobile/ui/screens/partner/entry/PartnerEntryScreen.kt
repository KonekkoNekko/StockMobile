@file:OptIn(ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.partner.entry

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.Coordinate
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.DialogWindow
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PartnerEntryScreen(
    navController: NavHostController,
    name: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val partnerEntryViewModel: PartnerEntryViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(context)
        )
    )

    val partnerDetails by partnerEntryViewModel.partnerDetails.collectAsState()
    val imageUrl by partnerEntryViewModel.imageUrl.collectAsState()
    val isSaving by partnerEntryViewModel.isSaving.collectAsState()
    val nameState by partnerEntryViewModel.name.collectAsState()
    val addressState by partnerEntryViewModel.address.collectAsState()
    val phoneState by partnerEntryViewModel.phone.collectAsState()
    val categoryState by partnerEntryViewModel.category.collectAsState()
    val coordinateState by partnerEntryViewModel.coordinate.collectAsState()
    val partnerLabels by partnerEntryViewModel.partnerLabels.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val location = savedStateHandle?.getLiveData<LatLng>("location")
    val address = savedStateHandle?.getLiveData<String>("address")

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<String>("imageUri")
            ?.observe(navController.currentBackStackEntry!!) { uri ->
                partnerEntryViewModel.setImageUrl(Uri.parse(uri))
            }
    }

    LaunchedEffect(location, address) {
        location?.observe(navController.currentBackStackEntry!!) { loc ->
            Log.d("PartnerEntryScreen", "Location observed: $loc")
            partnerEntryViewModel.coordinate.value =
                Coordinate("Selected Location", loc.latitude, loc.longitude)
        }
        address?.observe(navController.currentBackStackEntry!!) { addr ->
            Log.d("PartnerEntryScreen", "Address observed: $addr")
            partnerEntryViewModel.address.value = addr
        }
    }

    LaunchedEffect(partnerDetails) {
        partnerDetails?.let { details ->
            partnerEntryViewModel.name.value = details.partnerName
            partnerEntryViewModel.address.value = details.partnerAddress
            partnerEntryViewModel.phone.value = details.partnerPhone
            partnerEntryViewModel.category.value = details.partnerCategory
            partnerEntryViewModel.coordinate.value = Coordinate(
                lat = details.partnerCoordinate?.latitude ?: 0.0,
                lng = details.partnerCoordinate?.longitude ?: 0.0,
                name = details.partnerName
            )
            partnerEntryViewModel.partnerLabels.value = mapOf(
                "isClient" to details.partnerType.isClient,
                "isConsign" to details.partnerType.isConsign
            )

            // Add debugging logs
            Log.d("PartnerEntryScreen", "Fetched partner details: $details")
            Log.d(
                "PartnerEntryScreen",
                "Partner labels: ${partnerEntryViewModel.partnerLabels.value}"
            )
        }
    }

    LaunchedEffect(name) {
        if (name != null) {
            partnerEntryViewModel.fetchPartnerDetails(name)
        }
    }

    LaunchedEffect(Unit) {
        partnerEntryViewModel.fetchLocation()
    }

    val coroutineScope = rememberCoroutineScope()

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val folder = "partners"
                val filename = "${nameState}.jpg"
                navController.navigate("cameraScreen/$folder/$filename")
            } else {
                // Handle the case when the user denies the camera permission
            }
        }
    )

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    navController.navigate("mapsPickerScreen")
                } else {

                }
            }
        )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            if (name == null) {
                Text(text = "Tambah Mitra", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Edit Mitra", style = MaterialTheme.typography.titleMedium)
            }
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
        Surface(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = nameState,
                    onValueChange = { partnerEntryViewModel.name.value = it },
                    placeholder = { Text(text = "Masukkan Nama Mitra") },
                    label = { Text(text = "Nama Mitra") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = addressState,
                    onValueChange = { partnerEntryViewModel.address.value = it },
                    placeholder = { Text(text = "Masukkan Alamat Mitra") },
                    label = { Text(text = "Alamat Mitra") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = phoneState,
                    onValueChange = { partnerEntryViewModel.phone.value = it },
                    placeholder = { Text(text = "Masukkan Nomor Telepon Mitra") },
                    label = { Text(text = "Nomor Telepon Mitra") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = categoryState,
                    onValueChange = { partnerEntryViewModel.category.value = it },
                    placeholder = { Text(text = "Masukkan Bidang Mitra") },
                    label = { Text(text = "Bidang Mitra") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(text = "Pilih Label Mitra", style = MaterialTheme.typography.labelLarge)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalArrangement = Arrangement.Top
                    ) {
                        val chipLabels = listOf("isClient" to "Klien", "isConsign" to "Konsinyasi")
                        chipLabels.forEach { label ->
                            FilterChip(
                                selected = partnerLabels[label.first] == true,
                                onClick = {
                                    val newLabels =
                                        partnerEntryViewModel.partnerLabels.value.toMutableMap()
                                    newLabels[label.first] = !(newLabels[label.first] ?: false)
                                    partnerEntryViewModel.partnerLabels.value = newLabels
                                    Log.d("PartnerEntryScreen", "Updated labels: $newLabels")
                                },
                                label = { Text(label.second) },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .height(32.dp)
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 8.dp)
                        .size(250.dp),
                    border = BorderStroke(width = 1.dp, Color.LightGray)
                ) {
                    imageUrl?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Partner Image",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.FillHeight
                        )
                    }
                }
                Button(
                    onClick = {
                        if (nameState.isNotEmpty()) {
                            cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            showDialog.value = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Kamera",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Pilih Foto")
                }

                Text(
                    text = "Lokasi Mitra",
                    style = MaterialTheme.typography.titleMedium
                )

                Log.d("PartnerEntryScreen", "Coordinate: $coordinateState")
                coordinateState?.let {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(it.lat, it.lng), 12f
                        )
                    }
                    Box(
                        modifier = Modifier.size(330.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                        ) {
                            Marker(
                                state = MarkerState(
                                    position = LatLng(it.lat, it.lng)
                                ),
                                title = it.name ?: "Unknown Location",
                                tag = it.name ?: "Unknown Location"
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Start Delivery",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Atur Lokasi Mitra")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            partnerEntryViewModel.savePartner()
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "shouldRefetch",
                                true
                            )
                            navController.navigateUp()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(text = "Simpan")
                    }
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (name != null) {
                                partnerEntryViewModel.deletePartner(name)
                                navController.navigate(Screen.Partner.route)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Hapus Mitra")
                }
            }
        }
    }

    if (showDialog.value) {
        DialogWindow(
            titleText = "Nama Mitra Tidak Terisi",
            contentText = "Silahkan isi nama mitra terlebih dahulu",
            confirmText = "Baik",
            dismissText = "",
            clickConfirm = { showDialog.value = false },
            clickDismiss = { showDialog.value = false }
        )
    }
}
