package com.bielcode.stockmobile.ui.screens.transaction.gudang.transactiondeliveryconfirm

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.ProductItem
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.ProductCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.screens.transaction.transactiondetail.TransactionDetailViewModel
import com.bielcode.stockmobile.ui.screens.utility.formatDateString
import com.google.android.gms.location.LocationServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDeliveryConfirmScreen_Delivery(
    navController: NavController,
    transactionCode: String
) {
    val context = LocalContext.current
    val viewModel: TransactionDeliveryConfirmViewModel = viewModel(
        factory = ViewModelFactory(Injection.provideRepository(context))
    )
    val transaction by viewModel.transactionDetail.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionCode) {
        viewModel.fetchTransactionDetail(transactionCode)
    }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getLiveData<String>("imageUri")
            ?.observe(navController.currentBackStackEntry!!) { uri ->
                viewModel.setImageUrl(Uri.parse(uri))
            }
    }

    val cameraPermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val folder = "transactions"
                val filename = "${transactionCode}_delivered.jpg"
                navController.navigate("cameraScreen/$folder/$filename")
            } else {
                // Handle the case when the user denies the camera permission
            }
        }
    )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Konfirmasi Transaksi", style = MaterialTheme.typography.titleMedium)
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
                            Text(text = "Destinasi", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = transactionItem.transactionDestination,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(text = "Alamat", style = MaterialTheme.typography.titleMedium)
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
                            onClick = {
                                cameraPermissionResultLauncher.launch(Manifest.permission.CAMERA)
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
                                transactionItem.transactionItems.values.forEach { product ->
                                    ProductCard(
                                        name = product.itemName,
                                        size = product.itemSize,
                                        qty = product.itemQty
                                    )
                                }
                            }
                        }


                        Button(
                            onClick = {
                                viewModel.updateTransactionDocumentationUrl(
                                    transactionCode
                                )
                                transaction?.transactionItems?.values?.forEach { item ->
                                    viewModel.decreaseStock(item.itemCatalog, item.itemSize, item.itemQty)
                                }
                                navController.navigate("${Screen.Transaction.route}")
                            },
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
    }
}