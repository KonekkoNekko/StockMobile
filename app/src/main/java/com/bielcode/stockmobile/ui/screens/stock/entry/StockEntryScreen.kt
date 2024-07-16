@file:OptIn(ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.stock.entry

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.bielcode.stockmobile.data.model.ProductDetail
import com.bielcode.stockmobile.data.model.Stock
import com.bielcode.stockmobile.ui.components.DialogWindow
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockEntryScreen(
    navController: NavHostController,
    catalog: String? = null,
) {
    val context = LocalContext.current
    val stockEntryViewModel: StockEntryViewModel = viewModel(
        factory = ViewModelFactory(
            Injection.provideRepository(context)
        )
    )

    val productDetails by stockEntryViewModel.productDetails.collectAsState()
    val imageUrl by stockEntryViewModel.imageUrl.collectAsState()

    val isSaving by stockEntryViewModel.isSaving.collectAsState()
    val name by stockEntryViewModel.name.collectAsState()
    val catalogNumber by stockEntryViewModel.catalogNumber.collectAsState()
    val price by stockEntryViewModel.price.collectAsState()
    val selectedChips by stockEntryViewModel.selectedChips.collectAsState()
    val showDialog = remember { mutableStateOf(false) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<String>("imageUri")
            ?.observe(navController.currentBackStackEntry!!) { uri ->
                stockEntryViewModel.setImageUrl(Uri.parse(uri))
            }
    }

    LaunchedEffect(productDetails) {
        productDetails?.let { details ->
            stockEntryViewModel.name.value = details.productName
            stockEntryViewModel.catalogNumber.value = details.productCatalog
            stockEntryViewModel.price.value = details.productPrice.toString()
            stockEntryViewModel.selectedChips.value = details.productDetails.keys.toList()
        }
    }

    LaunchedEffect(catalog) {
        if (catalog != null) {
            stockEntryViewModel.fetchProductDetails(catalog)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val folder = "images"
                val filename = "${catalogNumber}.png"
                navController.navigate("cameraScreen/$folder/$filename")
            } else {
                // Handle the case when the user denies the camera permission
            }
        }
    )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            if (catalog == null) {
                Text(text = "Tambah Stok Baru", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Edit Stok Baru", style = MaterialTheme.typography.titleMedium)
            }
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
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
                    value = name,
                    onValueChange = { stockEntryViewModel.name.value = it },
                    placeholder = { Text(text = "Masukkan Nama Produk") },
                    label = { Text(text = "Nama Produk") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = catalogNumber,
                    onValueChange = { stockEntryViewModel.catalogNumber.value = it },
                    placeholder = { Text(text = "Masukkan Katalog Produk") },
                    label = { Text(text = "Nomor Katalog Produk") },
                    modifier = Modifier.fillMaxWidth(),
                )
                TextField(
                    value = price,
                    onValueChange = { stockEntryViewModel.price.value = it },
                    placeholder = { Text(text = "Masukkan Harga Jual") },
                    label = { Text(text = "Harga Jual") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(text = "Pilih Ukuran Produk", style = MaterialTheme.typography.labelLarge)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalArrangement = Arrangement.Top
                    ) {
                        val chipLabels =
                            listOf("L", "M", "S", "X", "XL", "XXL", "XXXL", "Universal")
                        chipLabels.forEach { label ->
                            FilterChip(
                                selected = selectedChips.contains(label),
                                onClick = {
                                    if (selectedChips.contains(label)) {
                                        stockEntryViewModel.selectedChips.value -= label
                                    } else {
                                        stockEntryViewModel.selectedChips.value += label
                                    }
                                },
                                label = {
                                    Text(text = label)
                                },
                                leadingIcon = {
                                    if (selectedChips.contains(label)) {
                                        Icon(
                                            imageVector = Icons.Default.Done,
                                            contentDescription = "Selected",
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    } else {
                                        null
                                    }
                                },
                            )
                        }
                    }
                }

                Text(text = "Pilih Gambar Produk", style = MaterialTheme.typography.labelLarge)
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(size = 12.dp),
                    border = BorderStroke(width = 1.dp, Color.LightGray)
                ) {
                    imageUrl?.let { uri ->
                        Log.d("URI", uri.toString())
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = "Product Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.FillHeight
                        )
                    } ?: Image(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillHeight
                    )
                }
                Button(
                    onClick = {
                        if (catalogNumber.isNotEmpty()) {
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
                    Text(text = "Ambil Foto")
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            val stock = Stock(
                                productName = name,
                                productCatalog = catalogNumber,
                                productPrice = price.toIntOrNull() ?: 0,
                                productDetails = selectedChips.associateWith {
                                    ProductDetail(
                                        stockInitial = 0,
                                        stockCurrent = 0,
                                        stockSold = 0,
                                        stockConsign = 0
                                    )
                                },
                                productPhotoUrl = "images/$catalogNumber.png" // Ensure the photo URL format
                            )
                            stockEntryViewModel.saveProduct(stock)
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
                            if (catalog != null) {
                                stockEntryViewModel.deleteProduct(catalog)
                                navController.navigate(Screen.Stock.route)
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
                    Text(text = "Hapus Produk")
                }
            }
        }
    }

    if (showDialog.value) {
        DialogWindow(
            titleText = "Nomor Katalog Tidak Terisi",
            contentText = "Silahkan isi nomor katalog produk terlebih dahulu",
            confirmText = "Baik",
            dismissText = "",
            clickConfirm = { showDialog.value = false },
            clickDismiss = { showDialog.value = false }
        )
    }
}

