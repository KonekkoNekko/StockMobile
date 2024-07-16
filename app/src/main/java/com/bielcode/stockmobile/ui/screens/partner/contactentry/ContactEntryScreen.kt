package com.bielcode.stockmobile.ui.screens.partner.contactentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEntryScreen(
    partnerName: String,
    contactName: String? = null,
    contactPosition: String? = null,
    contactPhone: String? = null,
    onSave: () -> Unit = {},
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ContactEntryViewModel = viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    LaunchedEffect(contactName, contactPosition, contactPhone) {
        viewModel.setContactDetails(contactName ?: "", contactPosition ?: "", contactPhone ?: "")
    }

    val name by viewModel.contactName.collectAsState()
    val position by viewModel.contactPosition.collectAsState()
    val phone by viewModel.contactPhone.collectAsState()

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            if (contactName == null && contactPosition == null && contactPhone == null) {
                Text(text = "Tambah Kontak Baru", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Edit Kontak", style = MaterialTheme.typography.titleMedium)
            }
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
                    onValueChange = { viewModel.setContactDetails(it, position, phone) },
                    placeholder = { Text(text = "Masukkan Nama Kontak") },
                    label = { Text(text = "Nama Kontak") },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextField(
                    value = position,
                    onValueChange = { viewModel.setContactDetails(name, it, phone) },
                    placeholder = { Text(text = "Masukkan Jabatan Kontak") },
                    label = { Text(text = "Jabatan Kontak") },
                    modifier = Modifier.fillMaxWidth(),
                )

                TextField(
                    value = phone,
                    onValueChange = { viewModel.setContactDetails(name, position, it) },
                    placeholder = { Text(text = "Masukkan Nomor Kontak") },
                    label = { Text(text = "Nomor Kontak") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Button(
                    onClick = {
                        viewModel.saveContact(partnerName)
                        onSave()
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Simpan")
                }
                onDelete?.let {
                    Button(
                        onClick = {
                            viewModel.deleteContact(partnerName)
                            onDelete()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Hapus Kontak")
                    }
                }
            }
        }
    }
}
