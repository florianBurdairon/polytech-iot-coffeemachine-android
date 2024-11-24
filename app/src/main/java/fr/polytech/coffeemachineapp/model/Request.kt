package fr.polytech.coffeemachineapp.model

data class RequestRaw (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: String = ""
)

data class Request (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)