package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.polytech.coffeemachineapp.model.Device

@Composable
fun DeviceList(devices: List<Device>, onDeviceClick: (Device) -> Unit) {
    LazyColumn {
        item {
            // Implement the UI for the header
            Text(text = "List of devices")
        }
        items(devices.size) { deviceIndex ->
            DeviceItem(device = devices[deviceIndex], onDeviceClick = onDeviceClick)
        }
    }
}

@Composable
fun DeviceItem(device: Device, onDeviceClick: (Device) -> Unit) {
    // Implement the UI for each device item
    Row(modifier = Modifier.clickable { onDeviceClick(device) }) {
        Text(text = device.name)
        Text(text = device.status)
    }
}