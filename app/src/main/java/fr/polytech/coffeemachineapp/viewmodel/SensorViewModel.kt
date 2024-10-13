package fr.polytech.coffeemachineapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.polytech.coffeemachineapp.model.Sensor
import fr.polytech.coffeemachineapp.model.SensorData
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private val sensorPath = "sensors"
    private var _sensors = MutableStateFlow(listOf<Sensor>())
    val sensorsState: StateFlow<List<Sensor>> = _sensors

    private val sensorListListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val sensorsList = mutableListOf<Sensor>()
            for (sensorSnapshot in snapshot.children) {
                val mac = sensorSnapshot.key
                val data = sensorSnapshot.getValue(SensorData::class.java)
                if (mac != null && data != null) {
                    sensorsList.add(Sensor(mac, data))
                }
            }
            _sensors.update { sensorsList }
            Log.d("Firebase", "Sensors: ${_sensors.value.size}")
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    init {
        viewModelScope.launch {
            firebaseRepository.addListener(sensorPath, sensorListListener)
        }
    }
}