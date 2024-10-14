package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.Device
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeviceViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private val devicesPath = "devices"
    private var _devices = MutableStateFlow(listOf<Device>())
    private var _selectedDevice = MutableStateFlow<Device?>(null)
    val devices : StateFlow<List<Device>> = _devices
    val selectedDevice : StateFlow<Device?> = _selectedDevice

    private val deviceListListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val deviceList = mutableListOf<Device>()
            dataSnapshot.children.forEach { deviceSnapshot ->
                val device = deviceSnapshot.getValue(Device::class.java)
                device?.let { deviceList.add(it) }
            }
            _devices.update { deviceList }
            Log.d("Firebase", "Devices: ${_devices.value.size}")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    private val deviceListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val device = dataSnapshot.getValue(Device::class.java)
            device?.let {
                _selectedDevice.update { device }
                Log.d("Firebase", "Device: ${_selectedDevice.value}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun getDevices() {
        viewModelScope.launch {
            firebaseRepository.addListener(devicesPath, deviceListListener)
        }
    }

    fun removeDevices() {
        viewModelScope.launch {
            firebaseRepository.removeListener(devicesPath, deviceListListener)
        }
    }

    fun selectDevice(mac: String) {
        viewModelScope.launch {
            when {
                _selectedDevice.value?.mac == mac -> { return@launch } // Do nothing if the device is already selected
                _selectedDevice.value?.mac != mac -> {
                    firebaseRepository.removeListener("$devicesPath/${_selectedDevice.value?.mac}", deviceListener)
                    firebaseRepository.addListener("$devicesPath/$mac", deviceListener)
                }
                else -> {
                    firebaseRepository.addListener("$devicesPath/$mac", deviceListener)
                }
            }
        }
    }

    fun unselectDevice() {
        viewModelScope.launch {
            firebaseRepository.removeListener("$devicesPath/${_selectedDevice.value?.mac}", deviceListener)
            _selectedDevice.update { null }
        }
    }
}