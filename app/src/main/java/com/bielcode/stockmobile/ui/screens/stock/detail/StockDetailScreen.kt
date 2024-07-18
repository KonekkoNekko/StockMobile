@file:OptIn(ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.stock.detail

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Input
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.formatPriceWithDots
import com.bielcode.stockmobile.ui.components.StockHistoryCard
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StockDetailScreen(
    catalog: String,
    initSize: String,
    navController: NavHostController
) {
    val context = LocalContext.current
    val stockDetailViewModel: StockDetailViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))

    val productDetails by stockDetailViewModel.productDetails.collectAsState()
    val imageUrl by stockDetailViewModel.imageUrl.collectAsState()
    val correctRole by stockDetailViewModel.isCorrectRole.collectAsState()
    val transactions by stockDetailViewModel.transactions.collectAsState()

    LaunchedEffect(catalog) {
        stockDetailViewModel.fetchProductDetails(catalog)
        stockDetailViewModel.fetchTransactionsByCatalog(catalog)
    }

    if (productDetails == null) {
        Log.d("StockDetailScreen", "Product details are null or loading... $productDetails")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else {
        Log.d("StockDetailScreen", "Product details: $productDetails")
        val initialChip = productDetails?.productDetails?.keys?.find { it == initSize }
            ?: productDetails?.productDetails?.keys?.firstOrNull() ?: ""
        val selectedChip = remember { mutableStateOf(initialChip) }

        Scaffold(
            floatingActionButton = {
                if (correctRole) {
                    FloatingActionButton(onClick = {
                        navController.navigate("barcodeScanner/${productDetails?.productCatalog}/${selectedChip.value}")
                    }) {
                        Icon(imageVector = Icons.Default.Input, contentDescription = "Stock Input")
                    }
                }
            },
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = "Rincian Stok", style = MaterialTheme.typography.titleMedium)
                }, navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "BackArrow",
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        navController.navigate("stockEntry?catalog=$catalog")
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "BackArrow",
                        )
                    }
                })
            }) {
            Box(
                modifier = Modifier
                    .padding(it)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .height(250.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(size = 12.dp),
                            border = BorderStroke(width = 1.dp, Color.LightGray),
                        ) {
                            imageUrl?.let {
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = "Product Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
                                    contentScale = ContentScale.FillHeight
                                )
                            }
                        }
                    }

                    item {
                        Column {
                            Text(
                                text = productDetails?.productCatalog ?: "",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = productDetails?.productName ?: "",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = "Rp. ${
                                    productDetails?.productPrice?.let {
                                        formatPriceWithDots(
                                            it
                                        )
                                    }
                                }",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }

                    item {
                        Text(text = "Ukuran Produk", style = MaterialTheme.typography.labelLarge)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                            ) {
                                productDetails?.productDetails?.forEach { (size, details) ->
                                    FilterChip(
                                        selected = selectedChip.value == size,
                                        onClick = {
                                            selectedChip.value = size
                                        },
                                        label = {
                                            Text(text = size)
                                        }
                                    )
                                }
                            }
                            val selectedQty =
                                productDetails?.productDetails?.get(selectedChip.value)
                            selectedQty?.let { qty ->
                                val maxQuantity = maxOf(
                                    qty.stockInitial,
                                    qty.stockCurrent,
                                    qty.stockSold,
                                    qty.stockConsign
                                )
                                Text(
                                    text = "Kuantitas Produk",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .size(75.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = qty.stockInitial.toString(),
                                                style = MaterialTheme.typography.headlineMedium,
                                                modifier = Modifier
                                            )
                                        }
                                        Text(text = "Awal")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = qty.stockCurrent / maxQuantity.toFloat(),
                                                modifier = Modifier.size(75.dp),
                                            )
                                            Text(
                                                text = qty.stockCurrent.toString(),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                        Text(text = "Gudang")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = qty.stockSold / maxQuantity.toFloat(),
                                                modifier = Modifier.size(75.dp),
                                            )
                                            Text(
                                                text = qty.stockSold.toString(),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                        Text(text = "Terjual")
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier.align(Alignment.CenterHorizontally),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                progress = qty.stockConsign / maxQuantity.toFloat(),
                                                modifier = Modifier.size(75.dp),
                                            )
                                            Text(
                                                text = qty.stockConsign.toString(),
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }
                                        Text(text = "Konsinyasi")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Peta Persebaran Produk",
                            style = MaterialTheme.typography.titleMedium
                        )

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    -7.2585614250219015, 112.74837128938759
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
                                transactions.forEach { transaction ->
                                    transaction.transactionCoordination?.let { coord ->
                                        Marker(
                                            state = MarkerState(position = LatLng(coord.latitude, coord.longitude)),
                                            title = transaction.transactionCode,
                                            snippet = transaction.transactionAddress
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Riwayat Transaksi",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(transactions) { transaction ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            StockHistoryCard(
                                code = transaction.transactionCode,
                                date = transaction.transactionDate?.toString() ?: "",
                                origin = transaction.transactionAddress,
                                incoming = if (transaction.transactionType == "in") transaction.transactionItems.values.sumOf { it.itemQty } else 0,
                                outgoing = if (transaction.transactionType == "out") transaction.transactionItems.values.sumOf { it.itemQty } else 0,
                            )
                        }
                    }
                }
            }
        }
    }
}

