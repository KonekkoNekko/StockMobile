package com.bielcode.stockmobile.ui.screens.transaction

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.TransactionStackedCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.theme.Purple6750A4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val correctRole by transactionViewModel.isCorrectRole.collectAsState()
    val transactionList by transactionViewModel.transactionList.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    val filteredTransactions = when (selectedFilter) {
        "Penjualan" -> transactionViewModel.filterByType("Penjualan")
        "Konsinyasi" -> transactionViewModel.filterByType("Konsinyasi")
        else -> transactionList
    }

    Scaffold(modifier = Modifier,
        floatingActionButton = {
            if (correctRole) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.TransactionEntry.route)
                    },
                    containerColor = Purple6750A4,
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Button")
                }
            }
        }) {
        Surface(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text(text = "Cari Transaksi") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "search logo"
                        )
                    }
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .align(alignment = Alignment.Start),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FilterChip(
                        selected = selectedFilter == "Penjualan",
                        onClick = { selectedFilter = "Penjualan" },
                        label = { Text(text = "Transaksi PENJUALAN") })
                    FilterChip(
                        selected = selectedFilter == "Konsinyasi",
                        onClick = { selectedFilter = "Konsinyasi" },
                        label = { Text(text = "Transaksi KONSINYASI") })
                    FilterChip(
                        selected = selectedFilter == "All",
                        onClick = { selectedFilter = "All" },
                        label = { Text(text = "Semua Transaksi") })
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredTransactions) { transaction ->
                        val status = transactionViewModel.getStatus(transaction)
                        TransactionStackedCard(
                            type = transaction.transactionType,
                            code = transaction.transactionCode,
                            status = status,
                            name = transaction.transactionDestination,
                            address = transaction.transactionAddress,
                            qty = transaction.transactionItems.values.sumOf { it.itemQty }
                        )
                    }
                }
            }
        }
    }
}
