package fr.polytech.coffeemachineapp.viewmodel

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.polytech.coffeemachineapp.repository.BluetoothRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BluetoothViewModel(private val bluetoothRepository: BluetoothRepository) : ViewModel() {

    private val _bluetoothDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val bluetoothDevices = _bluetoothDevices.asStateFlow()

    fun startScan() {
        Log.d("BluetoothViewModel", "startScan called")
        viewModelScope.launch {
            bluetoothRepository.getAvailableDevices().let { deviceList ->
                Log.d("BluetoothViewModel", "Available devices: ${deviceList.size}")
                _bluetoothDevices.update { deviceList }
            }
        }
    }
}