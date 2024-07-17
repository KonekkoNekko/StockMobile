@file:OptIn(ExperimentalMaterial3Api::class)

package com.bielcode.stockmobile.ui.screens.transaction.marketing.transactionentry

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.data.model.TransactionItem
import com.bielcode.stockmobile.ui.components.ContactCard
import com.bielcode.stockmobile.ui.components.DialogWindow
import com.bielcode.stockmobile.ui.components.ProductCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    navController: NavController,
    transactionCode: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val transactionEntryViewModel: TransactionEntryViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))

    val transactionType by transactionEntryViewModel.transactionType.collectAsState()
    val partners by transactionEntryViewModel.partners.collectAsState()
    val filteredPartners by transactionEntryViewModel.filteredPartners.collectAsState()
    val selectedPartner by transactionEntryViewModel.selectedPartner.collectAsState()
    val selectedContacts by transactionEntryViewModel.selectedContacts.collectAsState()
    val transactionDate by transactionEntryViewModel.transactionDate.collectAsState()
    val transactionItems by transactionEntryViewModel.transactionItems.collectAsState()
    val isSaving by transactionEntryViewModel.isSaving.collectAsState()
    val transaction by transactionEntryViewModel.transaction.collectAsState()
    val query by transactionEntryViewModel.query.collectAsState()
    val documentUris by transactionEntryViewModel.documentUris.collectAsState()

    var expand by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(transactionType) }
    var transactionDateString by remember { mutableStateOf("") }
    var expandDropdown by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogContent by remember { mutableStateOf("") }

    fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                transactionDateString = "$dayOfMonth/${month + 1}/$year"
                transactionEntryViewModel.setTransactionDate(
                    Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }.time
                )
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    LaunchedEffect(transactionCode) {
        if (transactionCode != null) {
            transactionEntryViewModel.fetchTransactionDetails(transactionCode)
            transactionEntryViewModel.loadDocumentUris(transactionCode)
        }
    }

    LaunchedEffect(transactionDate) {
        transactionDate?.let {
            transactionDateString = "${it.date}/${it.month + 1}/${it.year + 1900}"
        }
    }

    DisposableEffect(Unit) {
        // Memulihkan data saat kembali ke halaman
        transactionEntryViewModel.restoreTransactionState()

        // Simpan data ketika pengguna meninggalkan halaman
        onDispose {
            transactionEntryViewModel.updateTransactionInPreferences()
        }
    }

    fun validateCompleteInputs() {
        if (transactionType.isEmpty() || selectedPartner == null || transactionDate == null || transactionItems.isEmpty() || selectedContacts == null) {
            dialogTitle = "Input Tidak Lengkap"
            dialogContent = "Harap lengkapi semua input sebelum melanjutkan."
            showDialog = true
        }
    }

    fun validateInputs() {
        when {
            transactionType.isEmpty() -> {
                dialogTitle = "Input Tipe Transaksi Kosong"
                dialogContent = "Harap pilih tipe transaksi sebelum melanjutkan."
                showDialog = true
            }

            transactionDate == null -> {
                dialogTitle = "Input Tanggal Transaksi Kosong"
                dialogContent = "Harap pilih tanggal transaksi sebelum melanjutkan."
                showDialog = true
            }

            selectedPartner == null -> {
                dialogTitle = "Input Destinasi Transaksi Kosong"
                dialogContent = "Harap pilih destinasi transaksi sebelum melanjutkan."
                showDialog = true
            }

            else -> {
                // Input valid, lanjutkan tindakan
            }
        }
    }

    val isItemEmpty = transactionItems.isEmpty()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            if (transactionCode == null) {
                Text(text = "Tambah Transaksi Baru", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Edit Transaksi Baru", style = MaterialTheme.typography.titleMedium)
            }
        }, navigationIcon = {
            IconButton(onClick = {
                transactionEntryViewModel.clearTransactionData()  // Clear transaction data after saving
                navController.navigate(Screen.Transaction.route)
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expand,
                    onExpandedChange = { expand = !expand },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        value = selected,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        onValueChange = {},
                        label = { Text(text = "Pilih Tipe Transaksi") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expand,
                        onDismissRequest = { expand = false },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        listOf("Penjualan", "Konsinyasi").forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = item,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                },
                                onClick = {
                                    selected = item
                                    expand = false
                                    transactionEntryViewModel.setTransactionType(item)
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = transactionDateString,
                    readOnly = true,
                    onValueChange = {},
                    placeholder = { Text(text = "Pilih Tanggal Transaksi") },
                    label = { Text(text = "Tanggal Transaksi") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "")
                        }
                    },
                    isError = isItemEmpty,
                    supportingText = {
                        if (isItemEmpty) {
                            Text(
                                text = "Jangan Isi Kolom Ini Apabila Daftar Bawaan Kosong",
                                color = Color.Red
                            )
                        }
                    }
                )
                ExposedDropdownMenuBox(
                    expanded = expandDropdown,
                    onExpandedChange = { expandDropdown = !expandDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = query,
                        readOnly = isItemEmpty,
                        onValueChange = {
                            transactionEntryViewModel.setQuery(it)
                            transactionEntryViewModel.filterPartners(it)
                            expandDropdown = it.isNotEmpty()
                        },
                        label = { Text("Destinasi Transaksi") },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandDropdown)
                        },
                        isError = isItemEmpty,
                        supportingText = {
                            if (isItemEmpty) {
                                Text(
                                    text = "Jangan Isi Kolom Ini Apabila Daftar Bawaan Kosong",
                                    color = Color.Red
                                )
                            }
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = expandDropdown,
                        onDismissRequest = { expandDropdown = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        filteredPartners.forEach { partner ->
                            DropdownMenuItem(onClick = {
                                transactionEntryViewModel.selectPartner(partner)
                                expandDropdown = false
                            }, text = {
                                Text(partner.partnerName)
                            })
                        }
                    }
                }
                Column {
                    Text(text = "Daftar Kontak", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedContacts.forEach { (contact, isChecked) ->
                            ContactCard(
                                name = contact.contactName,
                                position = contact.contactPosition,
                                phone = contact.contactPhone,
                                checked = isChecked,
                                onCheckedChange = {
                                    transactionEntryViewModel.toggleContactSelection(contact)
                                }
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                ) {
                    Text(text = "Daftar Bawaan", style = MaterialTheme.typography.titleMedium)
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(transactionItems) { product ->
                            val dismissState = rememberDismissState(
                                confirmValueChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        coroutineScope.launch {
                                            transactionEntryViewModel.removeTransactionItem(product)
                                        }
                                    }
                                    true
                                }
                            )

                            SwipeToDismiss(
                                state = dismissState,
                                directions = setOf(DismissDirection.EndToStart),
                                background = {
                                    val color =
                                        if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                                            Color.Red
                                        } else {
                                            Color.Transparent
                                        }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White
                                        )
                                    }
                                },
                                dismissContent = {
                                    ProductCard(
                                        name = product.itemName,
                                        size = product.itemSize,
                                        qty = product.itemQty
                                    )
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.SearchAddProduct.route)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Produk",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = "Tambah Produk")
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Dokumen Terkait", style = MaterialTheme.typography.titleMedium)
                    Button(
                        onClick = {
                            validateInputs()
                            if (!showDialog) {
                                transactionEntryViewModel.saveTransactionToPreferences { transactionCode ->
                                    Log.d(
                                        "TransactionEntryScreen",
                                        "Navigating to DocumentScanner with transactionCode: $transactionCode"
                                    )
                                    navController.navigate("documentScanner/${transactionCode}?isForRead=false")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = "Scan Document",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = "Pindai Dokumen")
                    }
                }

                Button(
                    onClick = {
                        validateCompleteInputs()
                        if (!showDialog) {
                            if (transactionCode != null) {
                                transactionEntryViewModel.updateTransaction { transactionCode ->
                                    transactionEntryViewModel.clearTransactionData()  // Clear transaction data after saving
                                    navController.navigate(Screen.Transaction.route)
                                }
                            } else {
                                transactionEntryViewModel.saveTransaction { transactionCode ->
                                    transactionEntryViewModel.clearTransactionData()  // Clear transaction data after saving
                                    navController.navigate(Screen.Transaction.route)
                                }
                            }
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
                        Text(text = "Simpan Transaksi")
                    }
                }

                if (transactionCode != null) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                transactionEntryViewModel.deleteTransaction(transactionCode)
                                navController.navigate(Screen.Transaction.route)
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
                        Text(text = "Hapus Transaksi")
                    }
                }
            }
        }
    }

    if (showDialog) {
        DialogWindow(
            titleText = dialogTitle,
            contentText = dialogContent,
            confirmText = "OK",
            dismissText = "",
            clickConfirm = { showDialog = false },
            clickDismiss = {}
        )
    }
}
