package com.bielcode.stockmobile.ui.screens.utility.mapspicker

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedLocationCard(
    onConfirmRequest: () -> Unit,
    selectedAddress: String,
    latLng: LatLng,
) {
    Card(
        modifier = Modifier
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 16.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            ListItem(
                overlineContent = {
                    Text(text = "Selected Location:")
                },
                headlineContent = {
                    Text(
                        text = selectedAddress, color = MaterialTheme.colorScheme.primary
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedButton(onClick = {
                    onConfirmRequest()
                }) {
                    Text(text = "Confirm")
                }
            }
        }
    }
}
