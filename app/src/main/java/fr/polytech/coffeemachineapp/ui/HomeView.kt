package fr.polytech.coffeemachineapp.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.components.BluetoothDialog
import fr.polytech.coffeemachineapp.ui.components.DeviceList
import fr.polytech.coffeemachineapp.ui.components.NewDeviceButton
import fr.polytech.coffeemachineapp.ui.destinations.DeviceDetailViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination(start = true)
@Composable
fun HomeView(navigator: DestinationsNavigator, snackbarHostState: SnackbarHostState, snackbarScope: CoroutineScope) {
    val authViewModel: AuthViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()
    val deviceViewModel: DeviceViewModel = getViewModel()
    val devices by deviceViewModel.devices.collectAsState()

    val context = LocalContext.current

    var showRationale by rememberSaveable { mutableStateOf(false) }
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with Bluetooth operations
                snackbarScope.launch {
                    snackbarHostState.showSnackbar("Bluetooth permission granted")
                }
                launchBluetoothSetup(navigator, context)
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                snackbarScope.launch {
                    snackbarHostState.showSnackbar("Bluetooth permission denied")
                }
            }
        }
    )

    DisposableEffect(Unit) {
        deviceViewModel.getDevices()
        onDispose {
            deviceViewModel.removeDevices()
        }
    }

    BluetoothDialog(
        showRationale,
        showSettingsDialog,
        launcher,
        onDismissRationale = {
            showRationale = false
        },
        onDismissSettings = {
            showSettingsDialog = false
        }
    )

    Scaffold (
        floatingActionButton = {
            // Add a floating action button if needed
            if (authState is AuthState.Authenticated) {
                // Show the floating action button
                NewDeviceButton {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        // Permission already granted, proceed with Bluetooth operations
                        launchBluetoothSetup(navigator, context)
                    } else {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.BLUETOOTH_CONNECT)) {
                            // First time requesting permission, show rationale dialog
                            Log.d("HomeView", "First time requesting permission")
                            showRationale = true // Show rationale dialog
                        } else {
                            // Second time requesting permission, show settings dialog
                            Log.d("HomeView", "Second time requesting permission")
                            showSettingsDialog = true // Show settings dialog
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        when (authState) {
            is AuthState.Authenticated -> {
                Column (modifier = Modifier.padding(innerPadding)) {
                    DeviceList(devices = devices) {
                        // Handle device click
                        navigator.navigate(DeviceDetailViewDestination(it.mac))
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        onClick = {
                            navigator.navigate(LoginViewDestination)
                        },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text(text = "Sign in")
                    }
                }
            }
        }
    }
}

fun launchBluetoothSetup(navigator: DestinationsNavigator, context: Context) {
    // Navigate to the Bluetooth setup screen
    Toast.makeText(context, "Launching Bluetooth setup", Toast.LENGTH_SHORT).show()
}