@file:OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.transaction.TransactionDetail

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
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bielcode.stockmobile.data.Contact
import com.bielcode.stockmobile.data.Coordinate
import com.bielcode.stockmobile.data.ProductItem
import com.bielcode.stockmobile.ui.components.ContactCard_Call
import com.bielcode.stockmobile.ui.components.ProductCard_SizeQty
import com.bielcode.stockmobile.ui.components.ProductCard_SizeQtyChk
import com.bielcode.stockmobile.ui.screens.utility.formatDateString
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    code: String,
    type: String,
    date: String,
    destination: String,
    address: String,
    phone: String,
    contacts: List<Contact>,
    documentUrl: String,
    products: List<ProductItem>,
    coordinate: Coordinate,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Rincian Transaksi", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        }, actions = {
            IconButton(onClick = {}) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(text = "Kode Transaksi", style = MaterialTheme.typography.titleMedium)
                    Text(text = code, style = MaterialTheme.typography.headlineLarge)
                }

                Column {
                    Text(text = "Tipe Transaksi", style = MaterialTheme.typography.titleMedium)
                    Text(text = type, style = MaterialTheme.typography.headlineLarge)
                }


                Column {
                    Text(
                        text = "Tanggal Transaksi",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${formatDateString(date)}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Column {
                    Text(text = "Destinasi", style = MaterialTheme.typography.titleMedium)
                    Text(text = destination, style = MaterialTheme.typography.titleLarge)
                }

                Column {
                    Text(text = "Alamat", style = MaterialTheme.typography.titleMedium)
                    Text(text = address, style = MaterialTheme.typography.titleLarge)
                }

                Column {
                    Text(text = "Nomor Telepon", style = MaterialTheme.typography.titleMedium)
                    Text(text = phone, style = MaterialTheme.typography.titleLarge)
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Penerima", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        contacts.forEach { contact ->
                            ContactCard_Call(
                                name = contact.nameContact,
                                position = contact.positionContact,
                                phone = contact.numberContact
                            ) {

                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Dokumen Terkait", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Simpan")
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Daftar Bawaan", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        products.forEach { product ->
                            ProductCard_SizeQty(
                                name = product.name,
                                size = product.size,
                                qty = product.qty
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
                            coordinate.lat, coordinate.lng
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
                                position = LatLng(coordinate.lat, coordinate.lng)
                            ),
                            title = coordinate.name,
                            tag = coordinate.name
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen_Delivery(
    code: String,
    type: String,
    date: String,
    destination: String,
    address: String,
    phone: String,
    documentUrl: String,
    products: List<ProductItem>,
    coordinate: Coordinate,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Rincian Transaksi", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        }, actions = {
            IconButton(onClick = {}) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(text = "Kode Transaksi", style = MaterialTheme.typography.titleMedium)
                    Text(text = code, style = MaterialTheme.typography.headlineLarge)
                }

                Column {
                    Text(text = "Tipe Transaksi", style = MaterialTheme.typography.titleMedium)
                    Text(text = type, style = MaterialTheme.typography.headlineLarge)
                }


                Column {
                    Text(
                        text = "Tanggal Transaksi",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${formatDateString(date)}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                Column {
                    Text(text = "Destinasi", style = MaterialTheme.typography.titleMedium)
                    Text(text = destination, style = MaterialTheme.typography.titleLarge)
                }

                Column {
                    Text(text = "Alamat", style = MaterialTheme.typography.titleMedium)
                    Text(text = address, style = MaterialTheme.typography.titleLarge)
                }

                Column {
                    Text(text = "Nomor Telepon", style = MaterialTheme.typography.titleMedium)
                    Text(text = phone, style = MaterialTheme.typography.titleLarge)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Dokumen Terkait", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Simpan")
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Daftar Bawaan", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        products.forEach { product ->
                            ProductCard_SizeQtyChk(
                                name = product.name,
                                size = product.size,
                                qty = product.qty,
                                checked = false,
                                onIconClick = {}
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
                            coordinate.lat, coordinate.lng
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
                                position = LatLng(coordinate.lat, coordinate.lng)
                            ),
                            title = coordinate.name,
                            tag = coordinate.name
                        )
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(imageVector = Icons.Default.Directions, contentDescription = "Start Delivery")
                    Text(text = "Mulai Pengiriman")
                }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TransactionDetailScreenPreview() {
    TransactionDetailScreen(
        code = "TRX-SLD-123",
        type = "Penjualan",
        date = "2024-06-05",
        destination = "PT. Sumber Bahagia",
        address = "Jl. Raya Darmo 31-133 Surabaya 60241",
        phone = "0315886606",
        contacts = listOf(
            Contact("Ahmad Ridwan", "Manajer Pembelian", "08198765432"),
            Contact("Ahmad Ridwan", "Manajer Pembelian", "08198765432"),
            Contact("Ahmad Ridwan", "Manajer Pembelian", "08198765432")
        ),
        documentUrl = "",
        products = listOf(
            ProductItem("Post OP Knee Brace", "Universal", 30),
            ProductItem("Post OP Knee Brace", "Universal", 30),
            ProductItem("Post OP Knee Brace", "Universal", 30)
        ),
        coordinate = Coordinate("PT. Sumber Bahagia", -7.293738067463612, 112.73946771086807)
    )
}


@Preview(showSystemUi = true)
@Composable
fun TransactionDetailScreenDeliveryPreview() {
    TransactionDetailScreen_Delivery(
        code = "TRX-SLD-123",
        type = "Penjualan",
        date = "2024-06-05",
        destination = "PT. Sumber Bahagia",
        address = "Jl. Raya Darmo 31-133 Surabaya 60241",
        phone = "0315886606",
        documentUrl = "",
        products = listOf(
            ProductItem("Post OP Knee Brace", "Universal", 30),
            ProductItem("Post OP Knee Brace", "Universal", 30),
            ProductItem("Post OP Knee Brace", "Universal", 30)
        ),
        coordinate = Coordinate("PT. Sumber Bahagia", -7.293738067463612, 112.73946771086807)
    )
}