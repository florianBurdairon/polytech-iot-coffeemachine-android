package fr.polytech.coffeemachineapp.ui.components

import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BluetoothDeviceList(devices: List<BluetoothDevice>, header: (@Composable () -> Unit) = {}, onDeviceSelected: (BluetoothDevice) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Show the header if it is not null
        item {
            header()
        }
        // Show the list of devices
        items(devices.size) { deviceIndex ->
            BluetoothDeviceListItem(device = devices[deviceIndex], onDeviceSelected = onDeviceSelected)
        }
    }
}

@Composable
fun BluetoothDeviceListItem(device: BluetoothDevice, onDeviceSelected: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.shapes.medium
            )
            .clickable {
                onDeviceSelected(device)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = device.name ?: "Unknown",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = device.bondState.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(8.dp)
        )
    }
}