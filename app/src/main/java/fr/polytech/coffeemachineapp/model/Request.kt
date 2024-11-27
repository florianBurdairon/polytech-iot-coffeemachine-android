package fr.polytech.coffeemachineapp.model

import fr.polytech.coffeemachineapp.utils.RequestStatus

data class RequestRaw (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)

data class Request (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: RequestStatus = RequestStatus.UNKNOWN,
    val timestamp: Long = 0L
)