package com.bielcode.stockmobile.ui.screens.partner

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.components.PartnerStackedCard
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.theme.Purple6750A4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val partnerViewModel: PartnerViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))

    val partners by partnerViewModel.partners.collectAsState()
    val correctRole by partnerViewModel.isCorrectRole.collectAsState()
    val isLoading by partnerViewModel.isLoading.collectAsState()
    val imageUrls by partnerViewModel.imageUrls.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedPartnerTypes by remember { mutableStateOf(setOf<String>()) }

    val filteredPartners by remember {
        derivedStateOf {
            partners.filter { partner ->
                (searchQuery.isEmpty() || partner.partnerName.contains(
                    searchQuery,
                    ignoreCase = true
                )) &&
                        (selectedPartnerTypes.isEmpty() || selectedPartnerTypes.any { type ->
                            when (type) {
                                "Client" -> partner.partnerType.isClient
                                "Consign" -> partner.partnerType.isConsign
                                else -> false
                            }
                        })
            }
        }
    }

    Log.d("PartnerScreen", "partners: $partners")

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("shouldRefetch")
            ?.observe(navController.currentBackStackEntry!!) { shouldRefetch ->
                if (shouldRefetch) {
                    partnerViewModel.fetchPartners()
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("shouldRefetch")
                }
            }
    }

    Scaffold(
        modifier = Modifier,
        floatingActionButton = {
            if (correctRole) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.PartnerEntry.route)
                    },
                    containerColor = Purple6750A4,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Mitra"
                    )
                }
            }
        }
    ) {
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
                        partnerViewModel.setLoading(true)
                        partnerViewModel.setLoading(false)
                    },
                    placeholder = { Text(text = "Cari Mitra") },
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
                    listOf("Client", "Consign").forEach { type ->
                        FilterChip(
                            selected = selectedPartnerTypes.contains(type),
                            onClick = {
                                selectedPartnerTypes = if (selectedPartnerTypes.contains(type)) {
                                    selectedPartnerTypes - type
                                } else {
                                    selectedPartnerTypes + type
                                }
                                partnerViewModel.setLoading(true)
                                partnerViewModel.setLoading(false)
                            },
                            label = { Text(text = type) }
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
                        items(filteredPartners) { partner ->
                            Log.d(
                                "PartnerScreen",
                                "Partner: ${partner.partnerName}, isClient: ${partner.partnerType.isClient}, isConsign: ${partner.partnerType.isConsign}"
                            )
                            PartnerStackedCard(
                                isClient = partner.partnerType.isClient,
                                isConsign = partner.partnerType.isConsign,
                                name = partner.partnerName,
                                category = partner.partnerCategory,
                                imageUrl = imageUrls[partner.partnerName],
                                phone = partner.partnerPhone,
                                onClick = {
                                    navController.navigate("${Screen.PartnerDetail.route}/${partner.partnerName}")
                                },
                                context = context
                            )

                        }
                    }
                }
            }
        }
    }
}
