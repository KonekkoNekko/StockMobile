package com.bielcode.stockmobile.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bielcode.stockmobile.R
import com.bielcode.stockmobile.ui.theme.WhiteFEF7FF

@Composable
fun ContactCard(name: String, position: String, phone: String, checked: Boolean) {
    val (checkedStatus, setCheckedStatus) = remember {
        mutableStateOf(false)
    }

    LaunchedEffect(checked) {
        setCheckedStatus(checked)
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 250.dp, height = 110.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier,
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = position,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Black,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                ) {
                    Checkbox(checked = checkedStatus, onCheckedChange = { setCheckedStatus(it)})
                }
            }
        }
    }
}

@Composable
fun ContactCard_EditDelete(
    name: String,
    position: String,
    phone: String,
    onEditIcon: () -> Unit,
    onDeleteIcon: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 250.dp, height = 110.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier,
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = position,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Black,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp),
                verticalArrangement = Arrangement.Center
            ) {
                SmallFloatingActionButton(
                    onClick = onEditIcon, modifier = Modifier,
                    containerColor = Color.Blue, shape = CircleShape,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                    )
                }
                SmallFloatingActionButton(
                    onClick = onDeleteIcon, modifier = Modifier,
                    containerColor = Color.Red, shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Edit",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
fun ContactCard_Call(
    name: String,
    position: String,
    phone: String,
    onCallIcon: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = WhiteFEF7FF,
        ),
        shape = ShapeDefaults.Small,
        modifier = Modifier
            .size(width = 250.dp, height = 110.dp),
        border = BorderStroke(1.dp, Color.Cyan)
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier,
                    ) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = position,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Black,
                        )
                        Text(
                            text = phone,
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Black,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(2.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                IconButton(onClick = onCallIcon) {
                    Icon(
                        painter = painterResource(id = R.drawable.whatsapp),
                        contentDescription = "Edit",
                        tint = Color.Unspecified, modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContactCard() {
    ContactCard(
        name = "Aulia Hasna",
        position = "Accounting",
        phone = "087802776756",
        checked = true
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContactCard_EditDelete() {
    ContactCard_EditDelete(
        name = "Aulia Hasna",
        position = "Accounting",
        phone = "087802776756",
        onEditIcon = {},
        onDeleteIcon = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewContactCard_Call() {
    ContactCard_Call(
        name = "Aulia Hasna",
        position = "Accounting",
        phone = "087802776756",
        onCallIcon = {}
    )
}

