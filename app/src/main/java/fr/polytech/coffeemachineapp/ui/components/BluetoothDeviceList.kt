package fr.polytech.coffeemachineapp.ui.components

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceList(connectedDevices: List<BluetoothDevice>, pairedDevices: List<BluetoothDevice>, scannedDevices: List<BluetoothDevice>, onDeviceSelected: (BluetoothDevice) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (connectedDevices.isNotEmpty()) {
            item {
                Text(
                    text = "Connected Devices",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            // Show the list of connected devices
            items(connectedDevices) {
                BluetoothDeviceListItem(
                    device = it,
                    connectionState = BluetoothDeviceState.CONNECTED,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }
        if (pairedDevices.isNotEmpty()) {
            item {
                Text(
                    text = "Paired Devices",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            // Show the list of paired devices
            items(pairedDevices.filter { !connectedDevices.contains(it) }) {
                BluetoothDeviceListItem(
                    device = it,
                    connectionState = BluetoothDeviceState.PAIRED,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }
        if (scannedDevices.isNotEmpty()) {
            item {
                Text(
                    text = "Scanned Devices",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            // Show the list of scanned devices
            items(scannedDevices.filter { !pairedDevices.contains(it) && it.name != null }) {
                BluetoothDeviceListItem(
                    device = it,
                    connectionState = BluetoothDeviceState.NOT_PAIRED,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceListItem(device: BluetoothDevice, connectionState: BluetoothDeviceState, onDeviceSelected: (BluetoothDevice) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
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
        Column(
            modifier = Modifier.padding(8.dp).height(50.dp),
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Text(
                text = device.name ?: "(No name)",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = when(connectionState) {
                BluetoothDeviceState.CONNECTED -> "Connected"
                BluetoothDeviceState.PAIRED -> "Paired"
                BluetoothDeviceState.NOT_PAIRED -> "Not Paired"
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(8.dp)
        )
    }
}

enum class BluetoothDeviceState {
    CONNECTED,
    PAIRED,
    NOT_PAIRED
}