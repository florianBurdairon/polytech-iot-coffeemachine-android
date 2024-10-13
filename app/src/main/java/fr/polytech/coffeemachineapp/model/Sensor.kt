package fr.polytech.coffeemachineapp.model

data class Sensor (
    val mac: String,
    val data: SensorData
)

data class SensorData (
    val presence: Boolean? = null,
    val waterlevel: Double? = null
)