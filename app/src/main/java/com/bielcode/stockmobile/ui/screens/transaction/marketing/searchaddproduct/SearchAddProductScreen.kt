package com.bielcode.stockmobile.ui.screens.transaction.marketing.searchaddproduct

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.ProductStackedCard_SizeQty
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.screens.stock.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAddProductScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchAddProductViewModel: SearchAddProductViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))

    val stocks by searchAddProductViewModel.stocks.collectAsState()
    val isLoading by searchAddProductViewModel.isLoading.collectAsState()
    val imageUrls by searchAddProductViewModel.imageUrls.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSizes by remember { mutableStateOf(setOf<String>()) }

    val filteredStocks by remember {
        derivedStateOf {
            stocks.flatMap { stock ->
                stock.productDetails.map { (size, details) ->
                    Triple(stock, size, details)
                }
            }.filter { (stock, size, details) ->
                (searchQuery.isEmpty() || stock.productName.contains(
                    searchQuery,
                    ignoreCase = true
                )) &&
                        (selectedSizes.isEmpty() || selectedSizes.contains(size))
            }
        }
    }

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("shouldRefetch")
            ?.observe(navController.currentBackStackEntry!!) { shouldRefetch ->
                if (shouldRefetch) {
                    searchAddProductViewModel.fetchStocks()
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("shouldRefetch")
                }
            }
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(text = "Cari Produk", style = MaterialTheme.typography.titleMedium)
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
        Surface(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, start = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        searchAddProductViewModel.setLoading(true)
                        searchAddProductViewModel.setLoading(false)
                    },
                    placeholder = { Text(text = "Cari Produk") },
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
                    listOf("S", "M", "L", "XL", "XXL", "Universal").forEach { size ->
                        FilterChip(
                            selected = selectedSizes.contains(size),
                            onClick = {
                                selectedSizes = if (selectedSizes.contains(size)) {
                                    selectedSizes - size
                                } else {
                                    selectedSizes + size
                                }
                                searchAddProductViewModel.setLoading(true)
                                searchAddProductViewModel.setLoading(false)
                            },
                            label = { Text(text = "Size $size") }
                        )
                    }
                }
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredStocks) { (stock, size, details) ->
                            ProductStackedCard_SizeQty(
                                catalog = stock.productCatalog,
                                name = stock.productName,
                                size = size,
                                qty = details.stockCurrent,
                                imageUrl = imageUrls[stock.productCatalog],
                                onClick = {
                                    Log.d("SearchAddProductScreen", "Navigating to TransactionStockInputScreen with catalog: ${stock.productCatalog}, size: $size")
                                    navController.navigate(
                                        "${Screen.TransactionStockInput.route}/${stock.productCatalog}/$size"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
