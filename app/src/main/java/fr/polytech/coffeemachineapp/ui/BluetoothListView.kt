package fr.polytech.coffeemachineapp.ui

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.components.BluetoothDeviceList
import fr.polytech.coffeemachineapp.ui.components.ScanButton
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.utils.BLEScannerViewModel
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.BluetoothViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun BluetoothListView(navigator: DestinationsNavigator, scope: CoroutineScope) {
    // Get the view models
    val authViewModel: AuthViewModel = getViewModel()
    val bluetoothViewModel: BluetoothViewModel = getViewModel()
    val bleScannerViewModel: BLEScannerViewModel = getViewModel()

    // Get the state from the view models
    val authState by authViewModel.authState.collectAsState()
    val state by bluetoothViewModel.state.collectAsState()
    val scanResults by bleScannerViewModel.scanResults.collectAsState()

    val context = LocalContext.current

    var isScanning by rememberSaveable { mutableStateOf(false) }

    if (
        authState !is AuthState.Authenticated ||
        context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
    ) {
        navigator.popBackStack()
    }

    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable { navigator.navigate(HomeViewDestination) },
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            ScanButton(
                isScanning,
                onClick = {
                    if (!isScanning) {
                        bleScannerViewModel.startScan()
                        isScanning = true
                        scope.launch {
                            delay(30000) // Delay for 30 seconds
                            bleScannerViewModel.stopScan()
                            isScanning = false
                        }
                    }
                    else {
                        bleScannerViewModel.stopScan()
                        isScanning = false
                    }
                }
            )
        }
        BluetoothDeviceList(state.pairedDevices, scanResults.map { it.device }) { device ->
            Toast.makeText(context, "Connecting to ${device.name}", Toast.LENGTH_SHORT).show()
        }
    }
}