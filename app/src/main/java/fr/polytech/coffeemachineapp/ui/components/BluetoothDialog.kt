package fr.polytech.coffeemachineapp.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun BluetoothDialog(
    showSettingsDialog: Boolean,
    onDismissSettings: () -> Unit
) {
    val context = LocalContext.current

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { onDismissSettings() },
            title = { Text("Bluetooth Permission Required") },
            text = { Text("Bluetooth permission is required to use this app. Please grant it in the app settings.") },
            confirmButton = {
                Button(onClick = {
                    onDismissSettings()
                    // Launch intent to open app settings
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }) {
                    Text("Go to Settings")
                }
            },
            dismissButton = {
                Button(onClick = { onDismissSettings() }) {
                    Text("Cancel")
                }
            }
        )
    }
}