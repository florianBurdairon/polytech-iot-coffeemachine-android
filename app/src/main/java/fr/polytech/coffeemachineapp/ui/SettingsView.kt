package fr.polytech.coffeemachineapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun SettingsView(navigator: DestinationsNavigator) {
    val authViewModel: AuthViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (authState) {
            is AuthState.Authenticated -> {
                ElevatedButton(
                    onClick = {
                        authViewModel.signOut()
                        navigator.navigate(HomeViewDestination) {
                            Toast.makeText(context, "You sign out", Toast.LENGTH_SHORT).show()
                            popUpTo(0)
                        } //TODO Pop all back stack
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Sign out")
                }
            }
            else -> {
                ElevatedButton(
                    onClick = {
                        navigator.navigate(LoginViewDestination)
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Sign in")
                }
            }
        }
    }
}