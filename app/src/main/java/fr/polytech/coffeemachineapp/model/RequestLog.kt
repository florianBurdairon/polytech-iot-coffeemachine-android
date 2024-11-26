package fr.polytech.coffeemachineapp.model

data class RequestLogRaw (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: String = ""
)

data class RequestLog (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)