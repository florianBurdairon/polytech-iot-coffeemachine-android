package fr.polytech.coffeemachineapp.ui

import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.BLEViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun DeviceSetupView(navigator: DestinationsNavigator, scope: CoroutineScope, snackbarHostState: SnackbarHostState, bluetoothDevice: BluetoothDevice) {
    // Get the view models
    val authViewModel: AuthViewModel = getViewModel()
    val bleViewModel: BLEViewModel = getViewModel()
    val deviceViewModel: DeviceViewModel = getViewModel()

    // Get the state from the view models
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current

    var deviceName by rememberSaveable { mutableStateOf(bluetoothDevice.name ?: "") }
    var ssid by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var device by rememberSaveable { mutableStateOf<Device?>(null) }

    if (
        !bleViewModel.isBluetoothAvailable() ||
        authState !is AuthState.Authenticated ||
        context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
    ) {
        navigator.popBackStack()
    }

    Column {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
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
                    tint = colorScheme.onBackground
                )
                Text(
                    text = "Back",
                    style = typography.labelLarge,
                    color = colorScheme.onBackground,
                )
            }
        }
        Text(
            text = "Device setup",
            style = typography.headlineLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        )

        // Text field colors
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorScheme.primary,
            unfocusedBorderColor = colorScheme.primary,
            focusedLabelColor = colorScheme.primary,
            unfocusedLabelColor = colorScheme.primary,
            focusedTextColor = colorScheme.primary,
            unfocusedTextColor = colorScheme.primary,
            focusedPlaceholderColor = colorScheme.primary,
            unfocusedPlaceholderColor = colorScheme.primary,
            cursorColor = colorScheme.primary
        )

        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text("Device name") },
                colors = textFieldColors,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 8.dp)
            )
            HorizontalDivider(
                color = colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(16.dp)
            )
            OutlinedTextField(
                value = ssid,
                onValueChange = { ssid = it },
                label = { Text("Wifi SSID") },
                colors = textFieldColors,
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 8.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Wifi Password") },
                colors = textFieldColors,
                shape = RoundedCornerShape(15.dp),
                visualTransformation =
                if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                if (passwordVisible) R.drawable.visibility_off_24dp
                                else R.drawable.visibility_24dp
                            ),
                            contentDescription =
                            if (passwordVisible) "Hide password" else "Show password",
                            tint = colorScheme.primary
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 16.dp)
            )
            HorizontalDivider(
                color = colorScheme.primary,
                thickness = 1.dp,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    // Read the characteristic of the device
                    bleViewModel.readDeviceSetup(
                        bluetoothDevice,
                        onReadSuccess = { deviceData ->
                            // Read successful, parse the device data
                            Log.d("BLEViewModel", "Data received: $deviceData")
                            device = Gson().fromJson(deviceData, Device::class.java)

                            // Send the device setup
                            bleViewModel.sendDeviceSetup(
                                bluetoothDevice,
                                ssid,
                                password,
                                onWriteSuccess = {
                                    // Send the wifi credential successful, add the device to the database
                                    if (device != null) {
                                        deviceViewModel.addDevice(device!!.copy(name = deviceName))
                                        scope.launch {
                                            navigator.navigate(HomeViewDestination)
                                        }
                                    }
                                    else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error while sending device to database")
                                        }
                                    }
                                },
                                onWriteFailure = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Error while sending device setup")
                                    }
                                }
                            )
                        },
                        onReadFailure = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Error while reading characteristic")
                            }
                        }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primaryContainer,
                    contentColor = colorScheme.onPrimaryContainer
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Connect",
                    style = typography.labelLarge,
                    color = colorScheme.onPrimaryContainer
                )
            }
        }
    }
}