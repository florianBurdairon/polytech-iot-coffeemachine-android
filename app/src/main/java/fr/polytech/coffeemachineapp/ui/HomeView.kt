package fr.polytech.coffeemachineapp.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
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
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.model.QRData
import fr.polytech.coffeemachineapp.ui.components.DeviceList
import fr.polytech.coffeemachineapp.ui.components.NewDeviceButton
import fr.polytech.coffeemachineapp.ui.components.PermissionDialog
import fr.polytech.coffeemachineapp.ui.destinations.BluetoothListViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.DeviceDetailViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.utils.BarcodeScanner
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
import fr.polytech.coffeemachineapp.viewmodel.PermissionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination(start = true)
@Composable
fun HomeView(navigator: DestinationsNavigator, snackbarHostState: SnackbarHostState, snackbarScope: CoroutineScope) {
    // Get view models
    val authViewModel: AuthViewModel = getViewModel()
    val deviceViewModel: DeviceViewModel = getViewModel()
    val ownershipViewModel: OwnershipViewModel = getViewModel()
    val permissionsViewModel: PermissionsViewModel = getViewModel()

    // Collect states from view models
    val authState by authViewModel.authState.collectAsState()
    val devices by deviceViewModel.devices.collectAsState()
    val selectedOwner by ownershipViewModel.selectedOwnerState.collectAsState()
    val permissionState by permissionsViewModel.hasPermissions.collectAsState()

    // State variables
    var showBluetoothRationale by rememberSaveable { mutableStateOf(false) }
    var showCameraRationale by rememberSaveable { mutableStateOf(false) }
    var ownedDevices by rememberSaveable { mutableStateOf<List<Device>>(emptyList()) }

    val context = LocalContext.current

    // Barcode scanner instance
    val barcodeScanner = BarcodeScanner(context)

    // Permission launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsViewModel.updatePermissionStatus(permissionState.copy(hasCameraPermission = isGranted))
    }
    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        permissionsViewModel.updatePermissionStatus(permissionState.copy(hasBluetoothPermission = isGranted.values.all { it }))
    }

    // Function to check and request camera permission
    fun checkAndRequestCameraPermission(onGranted: () -> Unit = {}, onDenied: () -> Unit = {}) {
        // Permission to check and request for camera
        val cameraPermission = Manifest.permission.CAMERA
        when {
            // Check if camera permission is granted
            ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED -> {
                permissionsViewModel.updatePermissionStatus(permissionState.copy(hasCameraPermission = true))
                onGranted()
            }
            // Check if the user has previously denied the permission
            shouldShowRequestPermissionRationale(context as Activity, cameraPermission) -> {
                showCameraRationale = true
            }
            // Request the camera permission
            else -> {
                cameraPermissionLauncher.launch(cameraPermission)
                onDenied()
            }
        }
    }

    // Function to check and request Bluetooth permission
    fun checkAndRequestBluetoothPermission(onGranted: () -> Unit = {}, onDenied: () -> Unit = {}) {
        // List of permissions to check and request for Bluetooth
        val bluetoothPermissions = listOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
        when {
            // Check if all Bluetooth permissions are granted
            bluetoothPermissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED } -> {
                permissionsViewModel.updatePermissionStatus(permissionState.copy(hasBluetoothPermission = true))
                onGranted()
            }
            // Check if the user has previously denied the permissions
            bluetoothPermissions.any { shouldShowRequestPermissionRationale(context as Activity, it) } -> {
                showBluetoothRationale = true
            }
            // Request the Bluetooth permissions
            else -> {
                bluetoothPermissionLauncher.launch(bluetoothPermissions.toTypedArray())
                onDenied()
            }
        }
    }

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

    if (showBluetoothRationale) {
        // Show the bluetooth permission dialog
        PermissionDialog(
            context = context,
            dialogText = "This app needs bluetooth permissions to discover and connect to devices.",
            onConfirm = {
                showBluetoothRationale = false
            },
            onDismiss = {
                showBluetoothRationale = false
                snackbarScope.launch {
                    snackbarHostState.showSnackbar("Bluetooth permissions are required to set up new devices.")
                }
            }
        )
    }

    if (showCameraRationale) {
        // Show the camera permission dialog
        PermissionDialog(
            context = context,
            dialogText = "This app needs camera permissions to scan QR codes.",
            onConfirm = {
                showCameraRationale = false
            },
            onDismiss = {
                showCameraRationale = false
                snackbarScope.launch {
                    snackbarHostState.showSnackbar("Camera permissions are required to add devices to your account.")
                }
            }
        )
    }

    Scaffold (
        floatingActionButton = {
            // Add a floating action button if needed
            if (authState is AuthState.Authenticated) {
                // Show the floating action button
                NewDeviceButton(
                    onQRClick = {
                        // Handle QR code scanning
                        checkAndRequestCameraPermission(
                            onGranted = {
                                snackbarScope.launch {
                                    val qrDataJson = barcodeScanner.startScan()
                                    val qrData = qrDataJson?.let {
                                        try {
                                            Gson().fromJson(it, QRData::class.java)
                                        }
                                        catch (e: Exception) {
                                            Log.e("HomeView", "Error parsing QR data: ${e.message}")
                                            null
                                        }
                                    }
                                    snackbarHostState.showSnackbar("QR Data : ${qrData ?: "No data found"}")
                                }
                            },
                            onDenied = {
                                snackbarScope.launch {
                                    snackbarHostState.showSnackbar("Camera permissions are required to add devices to your account.")
                                }
                            }
                        )
                    },
                    onBluetoothClick = {
                        // Handle Bluetooth device selection
                        checkAndRequestBluetoothPermission(
                            onGranted = {
                                navigator.navigate(BluetoothListViewDestination)
                            },
                            onDenied = {
                                snackbarScope.launch {
                                    snackbarHostState.showSnackbar("Bluetooth permissions are required to set up new devices.")
                                }
                            }
                        )
                    }
                )
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

