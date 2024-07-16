@file:OptIn(ExperimentalLayoutApi::class)

package com.bielcode.stockmobile.ui.screens.transaction.marketing.TransactionEntry

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bielcode.stockmobile.data.ProductItem
import com.bielcode.stockmobile.data.model.Contact
import com.bielcode.stockmobile.ui.components.ContactCard
import com.bielcode.stockmobile.ui.components.ProductCard
import android.app.DatePickerDialog
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionEntryScreen(
    type: String? = null,
    date: String? = null,
    destination: String? = null,
    contacts: List<Pair<Contact, Boolean>>? = null,
    products: List<ProductItem>? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val types = arrayOf("Penjualan", "Konsinyasi")
    var expand by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(type ?: types[0]) }
    var transactionDate by remember { mutableStateOf(date ?: "") }
    fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                transactionDate = "$dayOfMonth/${month + 1}/$year"
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    val actualContacts = contacts ?: listOf(
        Pair(Contact("Ahmad Ridwan", "08198765432", "Manajer Pembelian"), false),
        Pair(Contact("Ahmad Ridwan", "08198765432", "Manajer Pembelian"), false),
        Pair(Contact("Ahmad Ridwan", "08198765432", "Manajer Pembelian"), false),
        Pair(Contact("Ahmad Ridwan", "08198765432", "Manajer Pembelian"), false),
    )

    val actualProducts = products ?: listOf(
        ProductItem("Post OP Knee Brace", "Universal", 30),
        ProductItem("Post OP Knee Brace", "Universal", 30),
        ProductItem("Post OP Knee Brace", "Universal", 30),
        ProductItem("Post OP Knee Brace", "Universal", 30),
    )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            if (type == null && date == null && destination == null) {
                Text(text = "Tambah Transaksi Baru", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Edit Transaksi Baru", style = MaterialTheme.typography.titleMedium)
            }
        }, navigationIcon = {
            IconButton(onClick = {}) {
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
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
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
                        types.forEach { item ->
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
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = transactionDate,
                    onValueChange = {},
                    placeholder = { Text(text = "Pilih Tanggal Transaksi") },
                    label = { Text(text = "Tanggal Transaksi") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "")
                        }
                    }
                )
                TextField(
                    value = destination ?: "",
                    onValueChange = {},
                    placeholder = { Text(text = "Ketik Destinasi") },
                    label = { Text(text = "Destinasi") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Daftar Kontak", style = MaterialTheme.typography.titleMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        actualContacts.forEach { contact ->
                            ContactCard(
                                name = contact.first.contactName,
                                position = contact.first.contactPosition,
                                phone = contact.first.contactPhone,
                                checked = contact.second,
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Daftar Bawaan", style = MaterialTheme.typography.titleMedium)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        actualProducts.forEach { product ->
                            ProductCard(name = product.name, size = product.size, qty = product.qty)
                        }
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Kamera",
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
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DocumentScanner,
                            contentDescription = "Kamera",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Text(text = "Pindai Dokumen")
                    }
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Simpan")
                }
                Button(
                    onClick = {},
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
}

@Preview(showSystemUi = true)
@Composable
fun AddTransactionEntryScreenPreview() {
    TransactionEntryScreen()
}

@Preview(showSystemUi = true)
@Composable
fun EditTransactionEntryScreenPreview() {
    TransactionEntryScreen(
        type = "Penjualan",
        date = "23 Juli 2024",
        destination = "RS. Ortopedi & Traumatologi",
        contacts = listOf(
            Pair(Contact("Ahmad Ridwan", "08198765432", "Manajer Pembelian"), false)
        ),
        products = listOf(
            ProductItem("Post OP Knee Brace", "Universal", 30)
        )
    )
}
