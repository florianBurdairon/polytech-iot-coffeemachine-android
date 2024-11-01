package fr.polytech.coffeemachineapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PermissionsViewModel: ViewModel() {
    private val _hasPermissions = MutableStateFlow(PermissionState())
    val hasPermissions: StateFlow<PermissionState> = _hasPermissions.asStateFlow()

    fun updatePermissionStatus(isGranted: PermissionState) {
        viewModelScope.launch {
            _hasPermissions.update { isGranted }
        }
    }
}

data class PermissionState(
    val hasCameraPermission: Boolean = false,
    val hasBluetoothPermission: Boolean = false
)