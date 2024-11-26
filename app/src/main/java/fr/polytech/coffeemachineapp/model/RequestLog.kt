package fr.polytech.coffeemachineapp.model

data class RequestLog (
    val mac: String,
    val uid: String,
    val timeStamp: Long,
    val action: String,
    val status: String
)