package fr.polytech.coffeemachineapp.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.polytech.coffeemachineapp.repository.BluetoothRepository
import fr.polytech.coffeemachineapp.ui.state.BluetoothUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {
    private val _state = MutableStateFlow(BluetoothUiState())
    val state = combine(
        bluetoothRepository.scannedDevices,
        bluetoothRepository.pairedDevices,
        _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    fun startScan() {
        bluetoothRepository.startDiscovery()
    }

    fun stopScan() {
        bluetoothRepository.stopDiscovery()
    }

    fun connectDevice(device: BluetoothDevice) {
        viewModelScope.launch {
            bluetoothRepository.connect(device)
            bluetoothRepository.write("Hello world")
            bluetoothRepository.disconnect()
        }
    }
}