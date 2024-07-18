package com.bielcode.stockmobile.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bielcode.stockmobile.ui.theme.WhiteFEF7FF


@Composable
fun ProductCard(name: String, size: String, qty: Int) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image and text layout
            AsyncImage(
                model = "https://placehold.co/100x200.png",
                contentDescription = "Product Photo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f / 1f)
            )
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        maxLines = 4
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Ukuran",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = size,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Kuantitas",
                        style = MaterialTheme.typography.bodySmall,
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



@Composable
fun TransactionCard(code: String, qty: Int, destination: String, type: String, date: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier.height(32.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                    )
                }
                Column(modifier = Modifier) {
                    Text(
                        text = "Ukuran",
                        style = MaterialTheme.typography.bodySmall,
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

            // Middle Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(modifier = Modifier) {
                    Text(
                        text = "Destinasi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = destination,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
                Column(modifier = Modifier) {
                    Text(
                        text = "Jenis Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
            }

            // End Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(modifier = Modifier) {
                    Text(
                        text = "Tanggal Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun StockHistoryCard(
    code: String,
    date: String,
    origin: String,
    incoming: Int,
    outgoing: Int,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier.height(32.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                    )
                }
                Column(modifier = Modifier) {
                    Text(
                        text = "Tgl. Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
            }

            // Middle Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1.5f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(modifier = Modifier.height(32.dp)) {
                    Text(
                        text = "Tujuan/Asal",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = origin,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                        maxLines = 1
                    )
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier) {
                        Text(
                            text = "Masuk",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = incoming.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                        )
                    }
                    Column(modifier = Modifier.padding(start = 28.dp)) {
                        Text(
                            text = "Keluar",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = Color.Black
                        )
                        Text(
                            text = outgoing.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                        )
                    }
                }
            }

            // End Column
//            Column(
//                modifier = Modifier
//                    .padding(vertical = 4.dp)
//                    .weight(0.5f),
//                verticalArrangement = Arrangement.spacedBy(6.dp)
//            ) {
//                Column(modifier = Modifier) {
//                    Text(
//                        text = "Sisa",
//                        style = MaterialTheme.typography.bodySmall,
//                        fontSize = 10.sp,
//                        color = Color.Black
//                    )
//                    Text(
//                        text = remain.toString(),
//                        style = MaterialTheme.typography.labelMedium,
//                        color = Color.Black
//                    )
//                }
//            }
        }
    }
}

@Composable
fun TransactionSimplefiedCard(code: String, qty: Int, type: String, date: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 300.dp, height = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Column(
                    modifier = Modifier.height(32.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black,
                    )
                }
                Column(modifier = Modifier) {
                    Text(
                        text = "Tgl. Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
            }

            // Middle Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Column(modifier = Modifier) {
                    Text(
                        text = "Jenis Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Black
                    )
                }
                Column(modifier = Modifier) {
                    Text(
                        text = "Kuantitas",
                        style = MaterialTheme.typography.bodySmall,
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

            // End Column
            Column(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProductCard() {
    MaterialTheme {
        ProductCard(
            name = "Knee Immobilizer",
            size = "M",
            qty = 50
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionCard() {
    MaterialTheme {
        TransactionCard(
            code = "TRX-123",
            qty = 20,
            destination = "RS. Darmo",
            type = "Konsinyasi",
            date = "2024-06-15"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStockHistoryCard() {
    MaterialTheme {
        StockHistoryCard(
            code = "TRX-120",
            date = "2024-06-15",
            origin = "PT. Sumber Bahagia",
            incoming = 10,
            outgoing = 20,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTransactionSimplifiedCard() {
    MaterialTheme {
        TransactionSimplefiedCard(
            code = "TRX-123",
            qty = 20,
            type = "Konsinyasi",
            date = "2024-06-15"
        )
    }
}