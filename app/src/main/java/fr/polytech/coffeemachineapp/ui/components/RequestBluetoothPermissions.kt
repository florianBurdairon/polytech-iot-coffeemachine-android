package fr.polytech.coffeemachineapp.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale

@Composable
fun RequestBluetoothPermissions(
    onPermissionsGranted: () -> Unit,
    onPermissionsDenied: () -> Unit
) {
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions.all { it.value }) {
                onPermissionsGranted()
            } else {
                showDialog = true
            }
        }
    )

    if (showDialog) {
        // Ask if should show the rationale
        if (
            shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.BLUETOOTH_CONNECT)
            || shouldShowRequestPermissionRationale(context, Manifest.permission.BLUETOOTH_SCAN)
        ) {
            // Show the rationale dialog
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Bluetooth Permissions Required") },
                text = { Text("This app needs Bluetooth permissions to discover and connect to devices.") },
                confirmButton = {
                    Button(onClick = {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                            )
                        )
                    }) {
                        Text("Grant Permissions")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        onPermissionsDenied()
                    }) {
                        Text("Deny")
                    }
                }
            )
        }
        else {
            // Show the settings dialog
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Bluetooth Permissions Required") },
                text = { Text("This app needs Bluetooth permissions to discover and connect to devices.") },
                confirmButton = {
                    Button(onClick = {
                        context.startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", context.packageName, null)
                        })
                    }) {
                        Text("Settings")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        showDialog = false
                        onPermissionsDenied()
                    }) {
                        Text("Deny")
                    }
                }
            )
        }
    } else {
        // Request permissions initially
        LaunchedEffect(key1 = Unit) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
                )
            )
        }
    }
}