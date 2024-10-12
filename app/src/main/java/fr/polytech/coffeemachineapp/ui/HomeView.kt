package fr.polytech.coffeemachineapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.components.DeviceList
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Destination(start = true)
@Composable
fun HomeView(navigator: DestinationsNavigator) {
    val authViewModel: AuthViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()
    val deviceViewModel: DeviceViewModel = getViewModel()
    val devices by deviceViewModel.devices.collectAsState()

    val context = LocalContext.current

    Scaffold (
        floatingActionButton = {
            // Add a floating action button if needed
            if (authState is AuthState.Authenticated) {
                // Show the floating action button
                ExtendedFloatingActionButton(
                    onClick = {
                        // Handle the click event
                        Toast.makeText(context, "Floating action button clicked", Toast.LENGTH_SHORT).show()
                        },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add device") },
                    text = { Text(text = "Add device") }
                )
            }
        }
    ) { innerPadding ->
        when (authState) {
            is AuthState.Authenticated -> {
                Column (modifier = Modifier.padding(innerPadding)) {
                    DeviceList(devices = devices) {
                        // Handle device click
                        Toast.makeText(context, "Device clicked: ${it.mac}", Toast.LENGTH_SHORT).show()
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