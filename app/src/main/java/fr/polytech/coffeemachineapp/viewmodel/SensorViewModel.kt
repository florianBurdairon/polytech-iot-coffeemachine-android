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
import fr.polytech.coffeemachineapp.utils.Constant.Companion.SENSORS_PATH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SensorViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {
    private var _selectedSensor = MutableStateFlow<Sensor?>(null)
    val selectedSensorState: StateFlow<Sensor?> = _selectedSensor

    private val sensorListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val mac = snapshot.key
            try {
                val data = snapshot.getValue(SensorData::class.java)
                if (mac != null && data != null) {
                    val sensor = Sensor(mac, data)
                    _selectedSensor.update { sensor }
                    Log.d("Firebase", "Sensor: $sensor")
                }
            }
            catch (e: Exception) {
                Log.e("Firebase", "Error: ${e.message}")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
            Log.e("Firebase", "Error: ${error.message}")
        }
    }

    fun selectSensor(mac: String) {
        viewModelScope.launch {
            when {
                _selectedSensor.value?.mac == mac -> { return@launch } // Do nothing if the sensor is already selected
                _selectedSensor.value?.mac != mac -> {
                    firebaseRepository.removeListener("$SENSORS_PATH/${_selectedSensor.value?.mac}", sensorListener)
                    firebaseRepository.addListener("$SENSORS_PATH/$mac", sensorListener)
                }
                else -> {
                    firebaseRepository.addListener("$SENSORS_PATH/$mac", sensorListener)
                }
            }
        }
    }

    fun unselectSensor() {
        viewModelScope.launch {
            firebaseRepository.removeListener("$SENSORS_PATH/${_selectedSensor.value?.mac}", sensorListener)
            _selectedSensor.update { null }
        }
    }
}