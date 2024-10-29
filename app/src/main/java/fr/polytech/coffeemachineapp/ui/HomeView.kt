package fr.polytech.coffeemachineapp.ui

import android.bluetooth.BluetoothManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.ui.components.DeviceList
import fr.polytech.coffeemachineapp.ui.components.NewDeviceButton
import fr.polytech.coffeemachineapp.ui.components.RequestBluetoothPermissions
import fr.polytech.coffeemachineapp.ui.destinations.BluetoothListViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.DeviceDetailViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
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
    val ownershipViewModel: OwnershipViewModel = getViewModel()
    val selectedOwner by ownershipViewModel.selectedOwnerState.collectAsState()

    val context = LocalContext.current

    var showPermissionRequest by rememberSaveable { mutableStateOf(false) }

    var ownedDevices by rememberSaveable { mutableStateOf<List<Device>>(emptyList()) }

    LaunchedEffect(devices, selectedOwner) {
        snapshotFlow {
            val allDevices = deviceViewModel.devices.value
            val owner = ownershipViewModel.selectedOwnerState.value
            val ownedDevicesList = mutableListOf<Device>()
            if (owner != null) {
                for (device in allDevices) {
                    for (ownership in owner.ownership) {
                        if (device.mac == ownership.mac) {
                            ownedDevicesList += device
                        }
                    }
                }
            }
            ownedDevicesList
        }.collect {
            ownedDevices = it
        }
    }

    DisposableEffect(Unit) {
        deviceViewModel.getDevices()
        Firebase.auth.currentUser?.uid?.let {
            ownershipViewModel.selectOwner(it)
        }
        onDispose {
            deviceViewModel.removeDevices()
            ownershipViewModel.unselectOwner()
        }
    }

    if (showPermissionRequest) {
        RequestBluetoothPermissions(
            onPermissionsGranted = {
                // Permissions granted, proceed with Bluetooth operations
                showPermissionRequest = false // Hide the dialog
                val isBluetoothEnabled = context.getSystemService(BluetoothManager::class.java).adapter?.isEnabled ?: false
                if (!isBluetoothEnabled) {
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar("Bluetooth is disabled. Please enable it to add a device.")
                    }
                } else {
                    navigator.navigate(BluetoothListViewDestination) // Navigate to the Bluetooth list view
                }
            },
            onPermissionsDenied = {
                // Handle permission denial
                showPermissionRequest = false // Hide the dialog
                snackbarScope.launch {
                    snackbarHostState.showSnackbar("Bluetooth permissions are required to use this app.")
                }
            }
        )
    }

    Scaffold (
        floatingActionButton = {
            // Add a floating action button if needed
            if (authState is AuthState.Authenticated) {
                // Show the floating action button
                NewDeviceButton {
                    showPermissionRequest = true
                }
            }
        }
    ) { innerPadding ->
        when (authState) {
            is AuthState.Authenticated -> {
                Column (modifier = Modifier.padding(innerPadding)) {
                    DeviceList(devices = ownedDevices) {
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