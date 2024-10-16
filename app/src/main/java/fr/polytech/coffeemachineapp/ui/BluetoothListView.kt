package fr.polytech.coffeemachineapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.ui.components.BluetoothDeviceList
import fr.polytech.coffeemachineapp.ui.components.DeviceStatusIcon
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.BluetoothViewModel
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun BluetoothListView(navigator: DestinationsNavigator) {
    val authViewModel: AuthViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()
    val bluetoothViewModel: BluetoothViewModel = getViewModel()
    val bluetoothDevices by bluetoothViewModel.bluetoothDevices.collectAsState()

    val context = LocalContext.current

    DisposableEffect(Unit) {
        bluetoothViewModel.startScan()
        onDispose { }
    }

    if (authState !is AuthState.Authenticated) {
        navigator.popBackStack()
    }
    // Show the bluetooth device list
    Column {
        // Back button
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .clickable { navigator.navigate(HomeViewDestination) },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Back",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { bluetoothViewModel.startScan() }
            )
        }

        // Show the list of bluetooth devices
        BluetoothDeviceList(
            bluetoothDevices,
            header = {
                Text(
                    text = "Bluetooth Devices",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
            },
        ) {
            Toast.makeText(
                context,
                "Device ${it.name} clicked",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}