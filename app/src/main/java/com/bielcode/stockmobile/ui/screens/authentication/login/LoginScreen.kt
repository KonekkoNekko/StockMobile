package com.bielcode.stockmobile.ui.screens.authentication.login

import android.util.Log
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.bielcode.stockmobile.ui.components.Banner
import com.bielcode.stockmobile.ui.theme.Purple6750A4
import com.bielcode.stockmobile.ui.theme.StockMobileTheme

@Composable
fun LoginScreen(
    toRegister: () -> Unit
) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel =
        viewModel(factory = ViewModelFactory(Injection.provideRepository(context)))
    val loginSuccess by loginViewModel.loginSuccess.collectAsState()
    val loginError by loginViewModel.loginError.collectAsState()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible = rememberSaveable { mutableStateOf(false) }
    val isEmailValid =
        email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val isPasswordValid = password.length >= 6

    // Success and Error Handling
    if (loginSuccess) {
        Toast.makeText(context, "Processing Login", Toast.LENGTH_SHORT).show()
    }

    if (loginError != null) {
        Toast.makeText(context, loginError.toString(), Toast.LENGTH_SHORT).show()
    }

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
                modifier = Modifier.padding(top = 80.dp, start = 32.dp, end = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Selamat Datang!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text(
                    text = "Silahkan Masuk menggunakan Akun yang Terdaftar",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 36.dp)
                        .align(Alignment.Start)
                )
                TextField(
                    value = email,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "emailIcon"
                        )
                    },
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    placeholder = { Text(text = "Masukkan e-mail Anda") },
                    supportingText = {
                        if (!isEmailValid && email.isNotEmpty()) {
                            Text(text = "Harap masukkan email yang valid!", color = Color.Red)
                        } else if(email.isEmpty()) {
                            Text(text = "Harap tidak mengosongi kolom email!", color = Color.Red)
                        } else {
                            Text(text = "")
                        }
                    },
                    isError = !isEmailValid && email.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                TextField(
                    value = password,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Password,
                            contentDescription = "passwordIcon"
                        )
                    },
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    placeholder = { Text(text = "Masukkan Password Anda") },
                    supportingText = {
                        if (!isPasswordValid && password.isNotEmpty()) {
                            Text(text = "Password harus minimal 6 karakter", color = Color.Red)
                        } else if (password.isEmpty()){
                            Text(text = "Harap tidak mengosongi kolom password!", color = Color.Red)
                        } else{
                            Text(text = "")
                        }
                    },
                    isError = !isPasswordValid && password.isNotEmpty(),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                Button(
                    onClick = {
                        loginViewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(vertical = 48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple6750A4)
                ) {
                    Text(text = "Lanjutkan")
                }
                Text(
                    text = "Belum Punya Akun?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 56.dp)
                )
                TextButton(
                    onClick = toRegister,
                    modifier = Modifier
                        .fillMaxWidth(0.7f),
                ) {
                    Text(
                        text = "Daftar Akun",
                        color = Purple6750A4,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
