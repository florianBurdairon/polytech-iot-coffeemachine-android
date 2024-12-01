package fr.polytech.coffeemachineapp.viewmodel

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val auth: FirebaseAuth) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential)
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.update { AuthState.Loading }
                auth.signInWithEmailAndPassword(email, password).await()
                if (auth.currentUser?.displayName == null) {
                    updateDisplayName(email.substringBefore('@'))
                }
                else {
                    _authState.update { AuthState.Authenticated(auth.currentUser) }
                }
            } catch (e: Exception) {
                _authState.update { AuthState.Error(e.message ?: "An error occurred while signing in") }
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.update { AuthState.Loading }
                auth.createUserWithEmailAndPassword(email, password).await()
                if (auth.currentUser?.displayName == null) {
                    updateDisplayName(email.substringBefore('@'))
                }
                else {
                    _authState.update { AuthState.Authenticated(auth.currentUser) }
                }
            } catch (e: Exception) {
                _authState.update { AuthState.Error(e.message ?: "An error occurred while creating an account") }
            }
        }
    }

    fun resetPassword(email: String, onPasswordResetEmailSent: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                onPasswordResetEmailSent()
            } catch (e: Exception) {
                _authState.update { AuthState.Error(e.message ?: "An error occurred while sending the password reset email") }
            }
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                _authState.update { AuthState.Loading }
                auth.signInWithCredential(credential).await()
                _authState.update { AuthState.Authenticated(auth.currentUser) }
            } catch (e: Exception) {
                _authState.update { AuthState.Error(e.message ?: "An error occurred while signing in") }
            }
        }
    }

    fun signOut(context: Context, onSignOut: () -> Unit) {
        val googleSignInClient = GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener {
            googleSignInClient.revokeAccess().addOnCompleteListener {
                auth.signOut()
                _authState.update { AuthState.Unauthenticated }
                onSignOut()
            }
        }
    }

    fun updateDisplayName(displayName: String) {
        val user = Firebase.auth.currentUser

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        viewModelScope.launch {
            _authState.update { AuthState.Updating }
            user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _authState.update { AuthState.Authenticated(auth.currentUser) }
                    } else {
                        _authState.update { AuthState.Error("Error updating user profile") }
                    }
                }
                ?.await()
        }
    }

    fun handleError(scope: CoroutineScope, snackbarHostState: SnackbarHostState) {
        viewModelScope.launch {
            val errorMessage = (_authState.value as AuthState.Error).message
            scope.launch {
                snackbarHostState.showSnackbar(errorMessage)
            }
            _authState.update { AuthState.Unauthenticated }
        }
    }

    init {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated(auth.currentUser)
        }
        else {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

sealed class AuthState {
    data object Unauthenticated : AuthState()
    data object Loading : AuthState()
    data object Updating : AuthState()
    data class Authenticated(val user: FirebaseUser?) : AuthState()
    data class Error(val message: String) : AuthState()
}