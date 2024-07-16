package com.bielcode.stockmobile.ui.screens.transaction.gudang.TransactionDeliveryConfirm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.data.ProductItem
import com.bielcode.stockmobile.ui.components.ProductCard
import com.bielcode.stockmobile.ui.screens.utility.formatDateString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDeliveryConfirmScreen_Delivery(
    code: String,
    type: String,
    date: String,
    destination: String,
    address: String,
    phone: String,
    documentUrl: String,
    products: List<ProductItem>,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Konfirmasi Transaksi", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {}) {
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

                Text(text = "Dokumentasi", style = MaterialTheme.typography.labelLarge)
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(size = 12.dp),
                    border = BorderStroke(width = 1.dp, Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.welcome),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Kamera",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Ambil Foto")
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daftar Bawaan",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Column(
                        modifier = Modifier,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        products.forEach { product ->
                            ProductCard(
                                name = product.name,
                                size = product.size,
                                qty = product.qty
                            )
                        }
                    }
                }


                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Finish Delivery",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(text = "Selesaikan Transaksi")
                }

            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TransactionDeliveryConfirmScreenPreview() {
    TransactionDeliveryConfirmScreen_Delivery(
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
        )
    )
}