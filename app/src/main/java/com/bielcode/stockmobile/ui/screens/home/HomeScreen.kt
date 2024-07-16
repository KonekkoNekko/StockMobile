package com.bielcode.stockmobile.ui.screens.home

import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.ui.screens.authentication.login.LoginViewModel
import com.bielcode.stockmobile.ui.theme.Purple6750A4


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val totalCurrentStock by homeViewModel.totalCurrentStock.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "logo",
                            modifier = Modifier.size(150.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        horizontalAlignment = Alignment.End,
                    ) {
                        IconButton(onClick = {
                            homeViewModel.logout()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                            )
                        }
                    }
                }
                Text(
                    text = "Selamat Datang",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = "Halo, Pengguna! Berikut ringkasan manajemen stok untuk bulan ini",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Purple6750A4,
                        contentColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 18.dp)
                    ) {
                        Text(
                            text = "Ringkasan Manajemen Stok",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "Kuantitas Stok Terkini",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                        Text(
                            text = "$totalCurrentStock Unit",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                        )
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Transaksi Berlangsung",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = "2 Transaksi",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Jumlah Mitra",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = "3 Mitra",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
