package fr.polytech.coffeemachineapp.model

data class WifiData (
    val ssid: String,
    val bssid: String,
    val level: Int,
    val frequency: Int,
    val capabilities: String
)