package com.bielcode.stockmobile.ui.screens.transaction

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.TransactionStackedCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.screens.stock.detail.StockDetailViewModel
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
                        .horizontalScroll(
                            rememberScrollState()
                        )
                        .align(alignment = Alignment.Start),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    FilterChip(
                        selected = false,
                        onClick = { /*TODO*/ },
                        label = { Text(text = "Transaksi PENJUALAN") })
                    FilterChip(
                        selected = false,
                        onClick = { /*TODO*/ },
                        label = { Text(text = "Transaksi KONSINYASI") })
                }
                LazyColumn(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(count = 10) {
                        TransactionStackedCard(
                            type = "Transaksi PENJUALAN",
                            code = "TRX-SLD-123",
                            status = "Siap Diantar",
                            name = "RS. Ortopedi & Traumalogi",
                            address = "Jl. Emerald Mansion TX 10, Jl. Lontar, Lidah Kulon, Kec. Lakarsantri, Surabaya, Jawa Timur 60213",
                            qty = 50
                        )
                    }
                }
            }
        }
    }
}