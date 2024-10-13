package fr.polytech.coffeemachineapp.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.LoginViewDestination
import fr.polytech.coffeemachineapp.viewmodel.AuthState
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@Destination
@Composable
fun LoginView(navigator: DestinationsNavigator, snackbarHostState: SnackbarHostState, snackBarScope: CoroutineScope) {
    val authViewModel: AuthViewModel = getViewModel()
    val authState by authViewModel.authState.collectAsState()

    val token = stringResource(id = R.string.web_client_id)
    val context = LocalContext.current

    val launcher = rememberFirebaseAuthLauncher(
        onAuthError = { _ ->
            snackBarScope.launch {
                snackbarHostState.showSnackbar("Google account sign in failed")
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (authState) {
            is AuthState.Authenticated -> {
                Toast.makeText(
                    context,
                    "Welcome ${(authState as AuthState.Authenticated).user?.displayName}",
                    Toast.LENGTH_SHORT
                ).show()
                navigator.navigate(HomeViewDestination){
                    popUpTo(LoginViewDestination.route) {
                        inclusive = true
                    }
                }
            }
            is AuthState.Unauthenticated -> {
                EmailPasswordLoginComponent(
                    snackbarHostState,
                    snackBarScope
                )
                HorizontalDivider(
                    color = colorScheme.primary,
                    thickness = 1.dp,
                    modifier = Modifier.padding(16.dp)
                )
                ElevatedButton(
                    onClick = {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(token)
                            .requestEmail()
                            .build()

                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        launcher.launch(googleSignInClient.signInIntent)
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
                    Text(text = "Sign in with Google")
                }
            }

            is AuthState.Error -> {
                LaunchedEffect(authState) {
                    snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                }
                authViewModel.errorHandled()
            }
            is AuthState.Loading -> {
                CircularProgressIndicator(
                    color = colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is AuthState.Updating -> {
                CircularProgressIndicator(
                    color = colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthError: (ApiException) -> Unit
) : ManagedActivityResultLauncher<Intent, ActivityResult> {

    val authViewModel: AuthViewModel = getViewModel()

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.signInWithGoogle(account?.idToken ?: "")
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}

@Composable
fun EmailPasswordLoginComponent(
    snackbarHostState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val authViewModel: AuthViewModel = getViewModel()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = colorScheme.primaryContainer,
        contentColor = colorScheme.onPrimaryContainer
    )
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
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth().padding(start = 32.dp, end = 32.dp, top = 8.dp, bottom = 16.dp)
        )
        ElevatedButton(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    snackBarScope.launch {
                        snackbarHostState.showSnackbar("Please enter email and password")
                    }
                    return@ElevatedButton
                }
                authViewModel.signInWithEmailAndPassword(email, password)
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(50.dp),
            colors = buttonColors
        ) {
            Text("Login")
        }

        ElevatedButton(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    snackBarScope.launch {
                        snackbarHostState.showSnackbar("Please enter email and password")
                    }
                    return@ElevatedButton
                }
                authViewModel.createUserWithEmailAndPassword(email, password)
            },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 8.dp)
                .fillMaxWidth()
                .height(50.dp),
            colors = buttonColors
        ) {
            Text("Register")
        }

        Text(
            text = "Reset password",
            color = colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(16.dp).clickable {
                if (email.isNotEmpty()) {
                    authViewModel.resetPassword(email) {
                        snackBarScope.launch {
                            snackbarHostState.showSnackbar("Password reset email sent")
                        }
                    }
                } else {
                    snackBarScope.launch {
                        snackbarHostState.showSnackbar("Please enter email")
                    }
                }
            }
        )
    }
}