@file:OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.utility.barcodescanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.ProductQtyDetail
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.formatPriceWithDots
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockInputScreen(
    navController: NavHostController,
    catalog: String,
    initSize: String
) {
    val context = LocalContext.current
    val stockInputViewModel: StockInputViewModel = viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val productDetails by stockInputViewModel.productDetails.collectAsState()
    val isSaving by stockInputViewModel.isSaving.collectAsState()
    val imageUrl by stockInputViewModel.imageUrl.collectAsState()

    LaunchedEffect(catalog) {
        stockInputViewModel.fetchProductDetails(catalog)
    }

    val initialChip = productDetails?.productDetails?.keys?.find { it == initSize }
        ?: productDetails?.productDetails?.keys?.firstOrNull() ?: ""
    val selectedChip = remember { mutableStateOf(initialChip) }
    val formattedPrice = productDetails?.productPrice?.let { formatPriceWithDots(it) } ?: ""

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Tambah Stok", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "BackArrow")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imageUrl?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = "Product Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp),
                    contentScale = ContentScale.Crop
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(text = catalog, style = MaterialTheme.typography.titleMedium)
                    Text(text = productDetails?.productName ?: "", style = MaterialTheme.typography.headlineLarge)
                    Text(text = "Rp. $formattedPrice", style = MaterialTheme.typography.titleLarge)
                }

                Text(text = "Ukuran Produk", style = MaterialTheme.typography.labelLarge)
                Column(modifier = Modifier.fillMaxWidth()) {
                    FlowRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                    ) {
                        productDetails?.productDetails?.forEach { (size, _) ->
                            FilterChip(
                                selected = selectedChip.value == size,
                                onClick = { selectedChip.value = size },
                                label = { Text(text = size) }
                            )
                        }
                    }
                }

                var quantity by remember { mutableStateOf("") }
                var destination by remember { mutableStateOf("") }
                var description by remember { mutableStateOf("") }
                var save_description = if (description != "") "($description)" else ""

                TextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    placeholder = { Text(text = "Masukkan Kuantitas Stok Masuk") },
                    label = { Text(text = "Kuantitas Stok Masuk") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = destination,
                    onValueChange = { destination = it },
                    placeholder = { Text(text = "Masukkan Barang Diterima Dari") },
                    label = { Text(text = "Terima Dari") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text(text = "Masukkan Keterangan (Opsional)") },
                    label = { Text(text = "Keterangan") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        val transactionItem = mapOf(
                            "item_1" to TransactionItem(
                                isChecked = false,
                                itemCatalog = catalog,
                                itemName = productDetails?.productName ?: "",
                                itemQty = quantity.toIntOrNull() ?: 0,
                                itemSize = selectedChip.value
                            )
                        )
                        stockInputViewModel.saveTransaction(
                            transactionItem,
                            "$destination $save_description",
                            "Masuk"
                        )
                        navController.navigate(Screen.Stock.route)
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
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionStockInputScreen(
    catalog: String,
    name: String,
    price: Int,
    productQuantities: List<ProductQtyDetail>,
    initSize: String,
) {
    val initialChip =
        productQuantities.find { it.size == initSize }?.size ?: productQuantities.first().size
    val selectedChip = remember {
        mutableStateOf(initialChip)
    }
    val formattedPrice = price?.let { formatPriceWithDots(it) } ?: ""



    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Tambah Bawaan", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        }
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            )
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(text = catalog, style = MaterialTheme.typography.titleMedium)
                    Text(text = name, style = MaterialTheme.typography.headlineLarge)
                    Text(
                        text = "Rp. $formattedPrice",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
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
                        productQuantities.forEach { item ->
                            FilterChip(
                                selected = selectedChip.value == item.size,
                                onClick = {
                                    selectedChip.value = item.size
                                },
                                label = {
                                    Text(text = item.size)
                                }
                            )
                        }
                    }
                }
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text(text = "Masukkan Kuantitas yang Akan Dikirim") },
                    label = { Text(text = "Kuantitas Bawaan") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Simpan")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckTransactionStockInputScreen(
    catalog: String,
    name: String,
    price: Int,
    productQuantities: List<ProductQtyDetail>,
    initSize: String,
    supposedQty: Int,
) {
    val initialChip =
        productQuantities.find { it.size == initSize }?.size ?: productQuantities.first().size
    val selectedChip = remember {
        mutableStateOf(initialChip)
    }
    val formattedPrice = price?.let { formatPriceWithDots(it) } ?: ""



    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Tambah Bawaan", style = MaterialTheme.typography.titleMedium)
        }, navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "BackArrow",
                )
            }
        }
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
            )
            Column(
                modifier = Modifier
                    .padding(32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column {
                    Text(text = catalog, style = MaterialTheme.typography.titleMedium)
                    Text(text = name, style = MaterialTheme.typography.headlineLarge)
                    Text(
                        text = "Rp. $formattedPrice",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
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
                        productQuantities.forEach { item ->
                            FilterChip(
                                selected = selectedChip.value == item.size,
                                onClick = {
                                    selectedChip.value = item.size
                                },
                                label = {
                                    Text(text = item.size)
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text(text = "Masukkan Kuantitas yang Akan Dikirim") },
                        label = { Text(text = "Kuantitas Bawaan") },
                        modifier = Modifier.weight(3f),
                    )
                    Text(
                        text = "/ $supposedQty",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text(text = "Simpan")
                }
            }
        }
    }
}
//
//@Preview(showSystemUi = true)
//@Composable
//fun PreviewInputBarcodeScreen() {
//    StockInputScreen(
//        catalog = "52016", name = "Knee Immobilizer", price = 865000, productQuantities = listOf(
//            ProductQtyDetail("L", 50,15, 60, 2),
//            ProductQtyDetail("M", 30,20, 10, 0),
//            ProductQtyDetail("S", 20,10, 60, 50)
//        ), initSize = "M"
//    )
//}

@Preview(showSystemUi = true)
@Composable
fun PreviewTransactionInputBarcodeScreen() {
    TransactionStockInputScreen(
        catalog = "52016",
        name = "Post OP Knee Brace Right",
        price = 1760000,
        productQuantities = listOf(
            ProductQtyDetail("Universal", 20,15, 60, 2)
        ),
        initSize = "Universal"
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewCheckTransactionInputBarcodeScreen() {
    CheckTransactionStockInputScreen(
        catalog = "52016",
        name = "Post OP Knee Brace Right",
        price = 1760000,
        productQuantities = listOf(
            ProductQtyDetail("Universal", 30,15, 60, 2)
        ),
        initSize = "Universal",
        supposedQty = 30
    )
}