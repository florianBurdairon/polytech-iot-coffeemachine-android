package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R

@Composable
fun NewDeviceButton(onBluetoothClick: () -> Unit, onQRClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isExpanded) {
            NewBluetoothDeviceButton(onClick = onBluetoothClick)
            NewQRDeviceButton(onClick = onQRClick)
        }
        FloatingActionButton(
            onClick = {
                isExpanded = !isExpanded
            },
            content = { Icon(painterResource(R.drawable.add_24dp), contentDescription = "Add device") }
        )
    }

}

@Composable
fun NewBluetoothDeviceButton(onClick: () -> Unit)
{
    ExtendedFloatingActionButton(
        onClick = {
            onClick()
        },
        icon = { Icon(painterResource(R.drawable.bluetooth_24dp), contentDescription = "Bluetooth") },
        text = { Text(text = "Bluetooth") },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
fun NewQRDeviceButton(onClick: () -> Unit)
{
    ExtendedFloatingActionButton(
        onClick = {
            onClick()
        },
        icon = { Icon(painterResource(R.drawable.qr_code_24dp), contentDescription = "QR Code") },
        text = { Text(text = "QR Code") },
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}