@file:OptIn(ExperimentalMaterial3Api::class)

package com.bielcode.stockmobile.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.rememberImagePainter
import com.bielcode.stockmobile.ui.theme.Purple6750A4
import com.bielcode.stockmobile.ui.theme.WhiteFEF7FF


@Composable
fun ProductStackedCard_SizeQty(
    catalog: String,
    name: String,
    size: String,
    qty: Int,
    imageUrl: Uri?,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 280.dp),
        border = BorderStroke(1.dp, Color.Cyan),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f)
            ) {
                imageUrl?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = catalog,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                    Text(
                        text = name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(2f)
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
fun PartnerStackedCard(
    isClient: Boolean,
    isConsign: Boolean,
    name: String,
    category: String,
    imageUrl: Uri?,
    phone: String,
    onClick: () -> Unit,
    context: Context
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 280.dp),
        border = BorderStroke(1.dp, Color.Cyan),
        onClick = onClick
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                imageUrl?.let {
                    Image(
                        painter = rememberImagePainter(it),
                        contentDescription = "Partner Image",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Row(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (isClient) {
                            SuggestionChip(onClick = {}, label = { Text(text = "Klien") })
                        }
                        if (isConsign) {
                            SuggestionChip(onClick = {}, label = { Text(text = "Konsinyasi") })
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(3f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmallFloatingActionButton(
                        onClick = {
                            val url = "tel:$phone"
                            val i = Intent(Intent.ACTION_DIAL)
                            i.data = Uri.parse(url)
                            startActivity(context, i, null)
                        }, modifier = Modifier,
                        containerColor = Color.DarkGray, shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Call",
                            tint = Color.White,
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun TransactionStackedCard(
    type: String,
    code: String,
    status: String,
    name: String,
    address: String,
    qty: Int
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 250.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                Column {
                    SuggestionChip(onClick = {}, label = { Text(text = type) })
                    Text(text = code, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Divider()
            Row(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth()
                    .padding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    SmallFloatingActionButton(
                        onClick = {}, modifier = Modifier,
                        containerColor = Purple6750A4, shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = "Edit",
                            tint = Color.White,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(3f)
                ) {
                    Text(text = name, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = address,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "Kuantitas", style = MaterialTheme.typography.labelSmall)
                    Text(text = qty.toString(), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductStackedCard_SizeQty_SizeQty() {
    MaterialTheme {
        ProductStackedCard_SizeQty(
            catalog = "52016",
            name = "Knee Immobilizer",
            size = "M",
            qty = 50,
            imageUrl = null
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewPartnerStackedCard() {
//    MaterialTheme {
//        PartnerStackedCard(
//            isClient = true,
//            isConsign = true,
//            name = "RS. Ortopedi & Traumatologi",
//            category = "Rumah Sakit",
//            imageUrl = null,
//            onClick = {},
//            phone = "08123456789",
//
//        )
//    }
//}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionStackedCard() {
    MaterialTheme {
        TransactionStackedCard(
            type = "Transaksi PENJUALAN",
            code = "TRX123",
            status = "Siap Diantar",
            name = "PT. Sumber Bahagia",
            address = "Jl. Raya Darmo 31-133 Surabaya 60241",
            qty = 30
        )
    }
}
