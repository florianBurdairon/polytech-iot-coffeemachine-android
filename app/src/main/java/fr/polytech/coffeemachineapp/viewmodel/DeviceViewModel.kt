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
    val devices : StateFlow<List<Device>> = _devices

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

    init {
        viewModelScope.launch {
            firebaseRepository.addListener(devicesPath, deviceListListener)
        }
    }
}