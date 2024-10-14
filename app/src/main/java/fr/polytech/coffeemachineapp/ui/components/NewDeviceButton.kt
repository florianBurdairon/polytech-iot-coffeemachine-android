package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun NewDeviceButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = {
            onClick()
        },
        icon = { Icon(Icons.Filled.Add, contentDescription = "Add device") },
        text = { Text(text = "Add device") }
    )
}