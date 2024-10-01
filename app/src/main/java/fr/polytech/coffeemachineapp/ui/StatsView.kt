package fr.polytech.coffeemachineapp.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination

@Destination
@Composable
fun StatsView(navigator: DestinationsNavigator) {
    val auth = Firebase.auth
    val user = auth.currentUser

    if (user != null) {
        // Display user settings

    }
    else {
        // Display login form
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