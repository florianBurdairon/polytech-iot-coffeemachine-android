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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R


@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceList(
    scannedDevices: List<BluetoothDevice>,
    isScanning: Boolean,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onScanStart: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 32.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detected Devices",
                    style = MaterialTheme.typography.headlineLarge,
                )
                if (!isScanning) {
                    Icon(
                        painter = painterResource(id = R.drawable.refresh_24dp),
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = "Refresh devices",
                        modifier = Modifier.size(32.dp).clickable { onScanStart() }
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        if (scannedDevices.isEmpty()) {
            item {
                Text(
                    text = "No devices found",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Show the list of scanned devices
            items(scannedDevices) {
                BluetoothDeviceListItem(
                    device = it,
                    onDeviceSelected = onDeviceSelected
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothDeviceListItem(device: BluetoothDevice, onDeviceSelected: (BluetoothDevice) -> Unit) {
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
        Icon(
            painter = painterResource(id = R.drawable.bluetooth_24dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "Bluetooth device",
            modifier = Modifier.size(24.dp).padding(16.dp)
        )
    }
}