package fr.polytech.coffeemachineapp.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.lightspark.composeqr.DotShape
import com.lightspark.composeqr.QrCodeColors
import com.lightspark.composeqr.QrCodeView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.model.QRData
import fr.polytech.coffeemachineapp.model.User
import fr.polytech.coffeemachineapp.ui.components.GuestList
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
import fr.polytech.coffeemachineapp.viewmodel.UserViewModel
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun DeviceSettingsView(navigator: DestinationsNavigator, device: Device) {
    val authViewModel: AuthViewModel = getViewModel()
    val ownershipViewModel: OwnershipViewModel = getViewModel()
    val userViewModel: UserViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()
    val guests by ownershipViewModel.guestsState.collectAsState()
    var user by rememberSaveable { mutableStateOf<String?>(null) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var guestUsers by rememberSaveable { mutableStateOf<List<User>>(listOf()) }

    DisposableEffect(Unit) {
        if (authState is AuthState.Authenticated) {
            user = (authState as AuthState.Authenticated).user?.uid
            user?.let { ownershipViewModel.getGuestList(device.mac, it) }
        }

        onDispose {
            if (authState is AuthState.Authenticated) {
                user = (authState as AuthState.Authenticated).user?.uid
                user?.let { ownershipViewModel.removeGuestListListener(device.mac, it) }
            }
        }
    }

    LaunchedEffect(guests) {
        guestUsers = guests.map {
            userViewModel.getUser(it) ?: User("Unknown", it)
        }
    }

    // Check if the user is authenticated
    if (authState !is AuthState.Authenticated) {
        // If not, navigate back to the home view
        navigator.popBackStack()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add guest for ${device.name}") },
            text = {
                val qrData = QRData(
                    (authState as AuthState.Authenticated).user?.uid.toString(),
                    device.mac
                )
                val qrDataJson = Gson().toJson(qrData)
                QrCodeView(
                    data = qrDataJson,
                    modifier = Modifier.size(250.dp).padding(16.dp),
                    colors = QrCodeColors(
                        background = MaterialTheme.colorScheme.primaryContainer,
                        foreground = MaterialTheme.colorScheme.primary,
                    ),
                    dotShape = DotShape.Circle
                )
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column {
        // Back button
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.clickable { navigator.navigateUp() },
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
        }
        // Display the list of guests
        GuestList(
            guests = guestUsers,
            header = {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Guests",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = "Add guest"
                        )
                    }
                }
            },
            onRemoveGuest = { guest ->
                Log.d("Firebase", "Removing guest $guest for device ${device.name}")
                user?.let { ownershipViewModel.removeGuest(it, guest, device.mac) }
            }
        )
    }
}