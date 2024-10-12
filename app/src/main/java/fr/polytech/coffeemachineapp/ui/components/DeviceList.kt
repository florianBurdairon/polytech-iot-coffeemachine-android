package fr.polytech.coffeemachineapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.model.Device

@Composable
fun DeviceList(devices: List<Device>, onDeviceClick: (Device) -> Unit) {
    LazyColumn {
        item {
            // Implement the UI for the header
            Text(text = "List of devices", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(16.dp))
        }
        items(devices.size) { deviceIndex ->
            DeviceItem(device = devices[deviceIndex], onDeviceClick = onDeviceClick)
        }
    }
}

@Composable
fun DeviceItem(device: Device, onDeviceClick: (Device) -> Unit) {
    // Implement the UI for each device item
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        .height(64.dp)
        .background(colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium)
        .clickable { onDeviceClick(device) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = device.name, color = colorScheme.onPrimaryContainer, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.weight(1f))
        DeviceStatusIcon(status = device.status)
    }
}

@Composable
fun DeviceStatusIcon(status: String) {
    when (status) {
        "online" -> Icon(
            painter = painterResource(id = R.drawable.check_circle_24dp),
            contentDescription = "device status : $status",
            modifier = Modifier.padding(end = 8.dp),
            tint = colorScheme.onPrimaryContainer
        )
        "offline" -> Icon(
            painter = painterResource(id = R.drawable.cancel_24dp),
            contentDescription = "device status : $status",
            modifier = Modifier.padding(end = 8.dp),
            tint = colorScheme.onPrimaryContainer
        )
        "busy" -> CircularProgressIndicator(
            color = colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(20.dp)
        )
        else -> {
            Log.d("DeviceStatusIcon", "Unknown status: $status")
            CircularProgressIndicator(
                color = colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
        }

    }
}