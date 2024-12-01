package fr.polytech.coffeemachineapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.components.ColumnChartCoffeeConsumption
import fr.polytech.coffeemachineapp.ui.components.PieChartDeviceUsage
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.utils.LogStatus
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.RequestLogViewModel
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun StatsView(navigator: DestinationsNavigator) {
    // Initialize the view models
    val authViewModel: AuthViewModel = getViewModel()
    val deviceViewModel: DeviceViewModel = getViewModel()
    val requestLogViewModel: RequestLogViewModel = getViewModel()

    // Collect the state from the view models
    val authState by authViewModel.authState.collectAsState()
    val devices by deviceViewModel.devices.collectAsState()
    val requestLogs by requestLogViewModel.requestLogs.collectAsState()

    DisposableEffect(Unit) {
        deviceViewModel.getDevices()
        requestLogViewModel.addAllRequestLogListener()
        onDispose {
            deviceViewModel.removeDevices()
            requestLogViewModel.removeAllRequestLogListener()
        }
    }
    when (authState) {
        is AuthState.Authenticated -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
            ) {
                item {
                    Text(
                        text = "Device Usage",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                    )
                    if (requestLogs.isNotEmpty()) {
                        PieChartDeviceUsage(
                            devices,
                            requestLogs.filter { it.uid == (authState as AuthState.Authenticated).user?.uid && it.status == LogStatus.SUCCESS  }
                        )
                    }
                    else {
                        Text(text = "No data", style = MaterialTheme.typography.titleMedium)
                    }
                }
                item {
                    Text(
                        text = "Coffee Consumption",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                        )
                    if (requestLogs.isNotEmpty()) {
                        ColumnChartCoffeeConsumption(
                            requestLogs.filter { it.uid == (authState as AuthState.Authenticated).user?.uid && it.status == LogStatus.SUCCESS }
                        )
                    }
                    else {
                        Text(text = "No data", style = MaterialTheme.typography.titleMedium)
                    }
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(text = "Sign in")
                }
            }
        }
    }
}