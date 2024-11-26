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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.ui.components.DeviceStatusIcon
import fr.polytech.coffeemachineapp.ui.components.RequestList
import fr.polytech.coffeemachineapp.ui.destinations.DeviceSettingsViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
import fr.polytech.coffeemachineapp.viewmodel.RequestLogViewModel
import fr.polytech.coffeemachineapp.viewmodel.RequestViewModel
import fr.polytech.coffeemachineapp.viewmodel.SensorViewModel
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun DeviceDetailView(navigator: DestinationsNavigator, mac: String) {
    // Initialize the view models
    val authViewModel: AuthViewModel = getViewModel()
    val deviceViewModel: DeviceViewModel = getViewModel()
    val sensorViewModel: SensorViewModel = getViewModel()
    val ownershipViewModel: OwnershipViewModel = getViewModel()
    val requestViewModel: RequestViewModel = getViewModel()
    val requestLogViewModel: RequestLogViewModel = getViewModel()

    // Collect the state from the view models
    val authState by authViewModel.authState.collectAsState()
    val selectedDevice by deviceViewModel.selectedDevice.collectAsState()
    val selectedSensor by sensorViewModel.selectedSensorState.collectAsState()
    val selectedOwner by ownershipViewModel.selectedOwnerState.collectAsState()
    val requests by requestViewModel.requests.collectAsState()
    val currentRequest by requestViewModel.currentRequest.collectAsState()
    val nextRequest by requestViewModel.nextRequest.collectAsState()
    val requestLogs by requestLogViewModel.requestLogs.collectAsState()

    val context = LocalContext.current

    val selectedDeviceMac by rememberSaveable { mutableStateOf(mac) }

    DisposableEffect(Unit) {
        deviceViewModel.selectDevice(selectedDeviceMac)
        sensorViewModel.selectSensor(selectedDeviceMac)
        ownershipViewModel.selectOwner((authState as AuthState.Authenticated).user?.uid ?: "")
        requestViewModel.addRequestsListener(selectedDeviceMac)
        requestLogViewModel.addRequestLogListener((authState as AuthState.Authenticated).user?.uid ?: "", selectedDeviceMac)

        onDispose {
            deviceViewModel.unselectDevice()
            sensorViewModel.unselectSensor()
            ownershipViewModel.unselectOwner()
            requestViewModel.removeRequestsListener(selectedDeviceMac)
            requestLogViewModel.removeRequestLogListener((authState as AuthState.Authenticated).user?.uid ?: "", selectedDeviceMac)
        }
    }

    // Check if the user is authenticated
    if (authState !is AuthState.Authenticated) {
        // If not, navigate back to the home view
        navigator.popBackStack()
    }

    // Show the device details view
    Column {
        // Back button
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
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
            // Show the settings icon if the user is the owner of the device
            var isOwner = false
            selectedOwner?.ownership?.forEach { ownership ->
                if (ownership.mac == selectedDeviceMac && ownership.type == "owner") isOwner = true
            }
            if (isOwner) {
                Icon(
                    painter = painterResource(R.drawable.settings_24dp),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable {
                        // Navigate to the device settings view
                        selectedDevice?.let { navigator.navigate(DeviceSettingsViewDestination(it)) }
                    }
                )
            }
        }
        // Show the device details panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
        ) {
            // Device name
            Text(
                text = selectedDevice?.name ?: "Unknown device",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 50.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            // Device details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device status
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Status:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    DeviceStatusIcon(selectedDevice?.status ?: "offline")
                }
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                // Water level
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Water level:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        WaterLevelIcon(selectedSensor?.data?.waterlevel ?: 0.0)
                        Text(
                            text = "${selectedSensor?.data?.waterlevel?.times(100) ?: "--"}%",
                        )
                    }
                }
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp),
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                // Is cup available
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cup available:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when (selectedSensor?.data?.presence) {
                            true -> "Yes"
                            false -> "No"
                            else -> "--"
                        }
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .height(100.dp)
                    .weight(0.5f)
                    .padding(start = 16.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = (selectedSensor?.data?.waterlevel ?: 0.0) > 0.25
                        && selectedSensor?.data?.presence == true
                        && selectedDevice?.status == "online",
                onClick = {
                    Toast.makeText(context, "1 Coffee requested", Toast.LENGTH_SHORT).show()
                }
            ) {
                // Text(text = "1 Coffee", style = MaterialTheme.typography.titleLarge)
                Icon(painter = painterResource(id = R.drawable.local_cafe), contentDescription = "1 Coffee", tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Button(
                modifier = Modifier
                    .height(100.dp)
                    .weight(0.5f)
                    .padding(start = 8.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                ),
                shape = MaterialTheme.shapes.medium,
                enabled = (selectedSensor?.data?.waterlevel ?: 0.0) > 0.25
                        && selectedSensor?.data?.presence == true
                        && selectedDevice?.status == "online",
                onClick = {
                    Toast.makeText(context, "2 Coffees requested", Toast.LENGTH_SHORT).show()
                }
            ) {
                // Text(text = "2 Coffees", style = MaterialTheme.typography.titleLarge)
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.local_cafe),
                        contentDescription = "2 Coffees",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.local_cafe),
                        contentDescription = "2 Coffees",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        // Show the requests list
        RequestList(currentRequest, nextRequest, requests, requestLogs)
    }
}

@Composable
fun WaterLevelIcon(waterLevel: Double) {
    when (waterLevel) {
        0.0 -> Icon(
            painter = painterResource(id = R.drawable.format_color_reset),
            contentDescription = "Water level: $waterLevel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        in 0.0..0.25 -> Icon(
            painter = painterResource(id = R.drawable.humidity_low),
            contentDescription = "Water level: $waterLevel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        in 0.25..0.5 -> Icon(
            painter = painterResource(id = R.drawable.humidity_mid),
            contentDescription = "Water level: $waterLevel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        in 0.5..1.0 -> Icon(
            painter = painterResource(id = R.drawable.humidity_high),
            contentDescription = "Water level: $waterLevel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        else -> Icon(
            painter = painterResource(id = R.drawable.format_color_reset),
            contentDescription = "Water level: $waterLevel",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}