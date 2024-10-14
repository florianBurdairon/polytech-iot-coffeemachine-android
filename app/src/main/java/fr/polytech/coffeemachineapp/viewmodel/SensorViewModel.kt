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
    private var _selectedSensor = MutableStateFlow<Sensor?>(null)
    val sensorsState: StateFlow<List<Sensor>> = _sensors
    val selectedSensorState: StateFlow<Sensor?> = _selectedSensor

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

    private val sensorListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val mac = snapshot.key
            val data = snapshot.getValue(SensorData::class.java)
            if (mac != null && data != null) {
                val sensor = Sensor(mac, data)
                _selectedSensor.update { sensor }
                Log.d("Firebase", "Sensor: $sensor")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun getSensors() {
        viewModelScope.launch {
            firebaseRepository.addListener(sensorPath, sensorListListener)
        }
    }

    fun removeSensors() {
        viewModelScope.launch {
            firebaseRepository.removeListener(sensorPath, sensorListListener)
        }
    }

    fun selectSensor(mac: String) {
        viewModelScope.launch {
            when {
                _selectedSensor.value?.mac == mac -> { return@launch } // Do nothing if the sensor is already selected
                _selectedSensor.value?.mac != mac -> {
                    firebaseRepository.removeListener("$sensorPath/${_selectedSensor.value?.mac}", sensorListener)
                    firebaseRepository.addListener("$sensorPath/$mac", sensorListener)
                }
                else -> {
                    firebaseRepository.addListener("$sensorPath/$mac", sensorListener)
                }
            }
        }
    }

    fun unselectSensor() {
        viewModelScope.launch {
            firebaseRepository.removeListener("$sensorPath/${_selectedSensor.value?.mac}", sensorListener)
            _selectedSensor.update { null }
        }
    }
}