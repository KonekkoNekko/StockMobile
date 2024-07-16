package com.bielcode.stockmobile.ui.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.ui.theme.Purple40
import com.bielcode.stockmobile.ui.theme.Purple6750A4

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    toLogin: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.welcome),
                        contentDescription = "Welcome",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f), // 50% opacity black
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(30.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "Semua dalam Genggaman Anda",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Kelola Stok, Cukup dengan Sentuhan!",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk dengan akun untuk melanjutkan",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = toLogin,
                        modifier = Modifier.width(200.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Purple6750A4)
                    ) {
                        Text(text = "Masuk")
                    }
                }
            }
        }
    }
}
//
//@Preview(showSystemUi = false)
//@Composable
//fun WelcomeScreenPreview() {
//    WelcomeScreen()
//}
