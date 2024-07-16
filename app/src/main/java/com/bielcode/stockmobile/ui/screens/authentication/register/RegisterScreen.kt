package com.bielcode.stockmobile.ui.screens.authentication.register

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bielcode.stockmobile.ViewModelFactory
import com.bielcode.stockmobile.data.injection.Injection
import com.bielcode.stockmobile.data.preferences.Account
import com.bielcode.stockmobile.ui.components.Banner
import com.bielcode.stockmobile.ui.theme.Purple6750A4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    toLogin: () -> Unit
) {
    val context = LocalContext.current
    val registerViewModel: RegisterViewModel = viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val registerSuccess by registerViewModel.registerSuccess.collectAsState()
    val registerError by registerViewModel.registerError.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val division = arrayOf("Gudang", "Marketing")
    var expand by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(division[0]) }
    var passwordVisible = rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        // Background Image
        Banner()

        // Welcome Card
        Card(
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .padding(top = 120.dp)
                .fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(top = 32.dp, start = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Daftar Akun",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )
                TextField(
                    value = name,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "name Icon"
                        )
                    },
                    onValueChange = { name = it},
                    label = { Text(text = "Nama Lengkap") },
                    placeholder = { Text(text = "Masukkan nama Anda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = phone,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "phone Icon"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = {phone = it},
                    label = { Text(text = "Nomor Telepon") },
                    placeholder = { Text(text = "Masukkan Nomor Telepon Anda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = email,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "email Icon"
                        )
                    },
                    onValueChange = {email = it},
                    label = { Text(text = "Email") },
                    placeholder = { Text(text = "Masukkan e-mail Anda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = password,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = "password Icon"
                        )
                    },
                    trailingIcon = {
                        val image = if (passwordVisible.value)
                            Icons.Default.Visibility
                        else Icons.Default.VisibilityOff

                        val description =
                            if (passwordVisible.value) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    onValueChange = {password = it},
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Masukkan Password Anda") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = expand,
                    onExpandedChange = { expand = !expand },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TextField(
                        value = selected,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        onValueChange = {},
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = "bagian divisi"
                            )
                        },
                        label = { Text(text = "Pilih Bagian Anda") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expand) },
                        modifier = Modifier.menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expand,
                        onDismissRequest = { expand = false },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        division.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item, style = MaterialTheme.typography.bodyLarge) },
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

                Button(
                    onClick = {
                        registerViewModel.register(email, password, Account(email, name, phone, selected))
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(top = 120.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple6750A4)
                ) {
                    Text(text = "Simpan")
                }
            }
        }
    }

    if (registerSuccess) {
        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
        toLogin()
    }

    registerError?.let { error ->
        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    Surface {
        RegisterScreen(toLogin = {})
    }
}
