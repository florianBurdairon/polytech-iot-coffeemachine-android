package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.polytech.coffeemachineapp.model.Device
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeviceViewModel : ViewModel() {
    val database = Firebase.database
    val deviceRef = database.getReference("devices")
    var _devices = MutableStateFlow(listOf<Device>())
    val devices : StateFlow<List<Device>> = _devices

    init {
        deviceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val deviceList = mutableListOf<Device>()
                for (deviceSnapshot in dataSnapshot.children) {
                    val device = deviceSnapshot.getValue(Device::class.java)
                    device?.let { deviceList.add(it) }
                }
                _devices.value = deviceList
                Log.d("Firebase", "Devices: ${_devices.value.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }
}