package com.bielcode.stockmobile.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bielcode.stockmobile.ui.theme.WhiteFEF7FF
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun PartnerCard(name: String, total: Int, status: List<String>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
//        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        var imageUrl by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(name) {
            coroutineScope.launch {
                imageUrl = getImageUrlFromFirebase("partners", "name", "jpg")
            }
        }
        Row {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = "https://placehold.co/100x200.png",
                    contentDescription = "Partner Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = total.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        status.forEach { statusItem ->
                            Text(
                                text = statusItem,
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard_SizeQty(name: String, size: String, qty: Int, catalog: String) {
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(name) {
        coroutineScope.launch {
            imageUrl = getImageUrlFromFirebase("images", catalog, "png")
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Partner Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ukuran",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = size,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kuantitas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = qty.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard_SizeOwnStats(name: String, size: String, own: Int, status: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = "",
                    contentDescription = "Partner Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ukuran",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = size,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Dimiliki",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = own.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = status,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard_SizeSldAvail(name: String, size: String, sold: Int, available: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = "https://placehold.co/100x200.png",
                    contentDescription = "Partner Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ukuran",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = size,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Terjual",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = sold.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tersedia",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = available.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard_SizeQtyChk(
    name: String,
    size: String,
    qty: Int,
    checked: Boolean = false,
    onIconClick: () -> Unit,
    catalog: String
) {
    val coroutineScope = rememberCoroutineScope()
    var imageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(name) {
        coroutineScope.launch {
            imageUrl = getImageUrlFromFirebase("images", catalog, "png")
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 150.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Partner Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ukuran",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = size,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                    Column(
                        modifier = Modifier.weight(2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kuantitas",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = qty.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (checked) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = "Checked",
                                modifier = Modifier.size(32.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "false",
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        SmallFloatingActionButton(
                            onClick = onIconClick, modifier = Modifier,
                            containerColor = Color.Blue, shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan",
                                tint = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewPartnerCard() {
//    MaterialTheme {
//        PartnerCard(
//            name = "Partner A",
//            total = 5,
//            status = listOf("Client", "Consignment")
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProductCard_SizeQty() {
//    MaterialTheme {
//        ProductCard_SizeQty(
//            name = "Knee Immobilizer",
//            size = "M",
//            qty = 50
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProductCard_SizeOwnStats() {
//    MaterialTheme {
//        ProductCard_SizeOwnStats(
//            name = "Knee Brace",
//            size = "L",
//            own = 15,
//            status = "Available"
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProductCard_SizeSldAvail() {
//    MaterialTheme {
//        ProductCard_SizeSldAvail(
//            name = "Ankle Support",
//            size = "S",
//            sold = 10,
//            available = 30
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProductCard_SizeQtyChk() {
//    MaterialTheme {
//        ProductCard_SizeQtyChk(
//            name = "Knee Immobilizer",
//            size = "M",
//            qty = 50,
//            checked = false,
//            onIconClick = {},
//        )
//    }
//}

suspend fun getImageUrlFromFirebase(folder: String, filename: String, extension: String): String? {
    return try {
        val storageRef = FirebaseStorage.getInstance().reference.child("$folder/$filename.$extension")
        storageRef.downloadUrl.await().toString()
    } catch (e: Exception) {
        Log.e("Firebase", "Error fetching image URL", e)
        null
    }
}
