@file:OptIn(ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.partner.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.ProductItemSizeOwnStts
import com.bielcode.stockmobile.data.TransactionSimplified
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.ContactCard_EditDelete
import com.bielcode.stockmobile.ui.components.ProductCard_SizeOwnStats
import com.bielcode.stockmobile.ui.components.TransactionSimplefiedCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerDetailScreen(
    name: String,
    navController: NavController
) {
    val context = LocalContext.current
    val partnerDetailViewModel: PartnerDetailViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val partnerDetails by partnerDetailViewModel.partnerDetails.collectAsState()
    val imageUrl by partnerDetailViewModel.imageUrl.collectAsState()
    val transactions by partnerDetailViewModel.transactions.collectAsState()
    val products by partnerDetailViewModel.products.collectAsState()

    LaunchedEffect(name) {
        Log.d("PartnerDetailScreen", "Launching effect for $name")
        partnerDetailViewModel.fetchPartnerDetails(name)
    }

    if (partnerDetails == null) {
        Log.d("PartnerDetailsScreen", "Partner details are null or loading... $partnerDetails")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        Log.d("PartnerDetailsScreen", "Partner details are not null... $partnerDetails")
        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Rincian Mitra", style = MaterialTheme.typography.titleMedium)
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
                    navController.navigate("${Screen.PartnerEntry.route}?name=$name")
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                    )
                }
            })
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(size = 12.dp),
                    border = BorderStroke(width = 1.dp, Color.LightGray)
                ) {
                    imageUrl?.let {
                        Image(
                            painter = rememberImagePainter(data = it),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column {
                    Text(
                        text = partnerDetails?.partnerName ?: "",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = partnerDetails?.partnerCategory ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Column {
                    Text(text = "Alamat", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = partnerDetails?.partnerAddress ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Column {
                    Text(text = "Nomor Telepon", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = partnerDetails?.partnerPhone ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Text(text = "Daftar Kontak", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .height(110.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    partnerDetails?.partnerContacts?.forEach { contact ->
                        ContactCard_EditDelete(
                            name = contact.contactName,
                            position = contact.contactPosition,
                            phone = contact.contactPhone,
                            onEditIcon = {
                                navController.navigate(
                                    "${Screen.ContactEntry.route}?partnerName=$name&contactName=${contact.contactName}&contactPosition=${contact.contactPosition}&contactPhone=${contact.contactPhone}"
                                )
                            },
                            onDeleteIcon = {
                                partnerDetailViewModel.deleteContact(name, contact.contactName)
                            }
                        )
                    }
                    SmallFloatingActionButton(onClick = {
                        navController.navigate("${Screen.ContactEntry.route}?partnerName=$name")
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                    }
                }

                Text(text = "Label Mitra", style = MaterialTheme.typography.labelLarge)
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                    ) {
                        if (partnerDetails?.partnerType?.isClient == true) {
                            SuggestionChip(onClick = {}, label = { Text(text = "Klien") })
                        }
                        if (partnerDetails?.partnerType?.isConsign == true) {
                            SuggestionChip(onClick = {}, label = { Text(text = "Konsinyasi") })
                        }
                    }
                }

                Text(
                    text = "Lokasi Mitra",
                    style = MaterialTheme.typography.titleMedium
                )

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            partnerDetails?.partnerCoordinate?.latitude ?: 0.0,
                            partnerDetails?.partnerCoordinate?.longitude ?: 0.0
                        ), 12f
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
                                position = LatLng(
                                    partnerDetails?.partnerCoordinate?.latitude ?: 0.0,
                                    partnerDetails?.partnerCoordinate?.longitude ?: 0.0
                                )
                            ),
                            title = partnerDetails?.partnerName ?: "",
                            tag = partnerDetails?.partnerName ?: ""
                        )
                    }
                }

                Button(
                    onClick = {
                        val name = partnerDetails?.partnerName
                        partnerDetails?.partnerCoordinate?.let { coordinate ->
                            openGoogleMaps(
                                context,
                                coordinate.latitude,
                                coordinate.longitude,
                                name
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Partner Location",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Lokasi Mitra")
                }

                Text(text = "Stok Terkait", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    products.forEach { product ->
                        ProductCard_SizeOwnStats(
                            name = product.name,
                            size = product.size,
                            own = product.owned,
                            status = product.status,
                        )
                    }
                }

                Text(text = "Riwayat Transaksi", style = MaterialTheme.typography.titleMedium)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    transactions.forEach { transaction ->
                        TransactionSimplefiedCard(
                            code = transaction.transactionCode,
                            qty = transaction.transactionItems.values.sumOf { it.itemQty },
                            type = transaction.transactionType,
                            date = transaction.transactionDate?.toString() ?: ""
                        )
                    }
                }
            }
        }
    }
}

fun openGoogleMaps(context: Context, latitude: Double, longitude: Double, label: String?) {
    val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($label)")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Handle the case when Google Maps is not installed
    }
}