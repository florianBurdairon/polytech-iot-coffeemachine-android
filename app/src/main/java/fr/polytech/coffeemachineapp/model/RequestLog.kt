package fr.polytech.coffeemachineapp.model

import fr.polytech.coffeemachineapp.utils.LogStatus

data class RequestLogRaw (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: String = "",
    val timestamp: Long = 0L
)

data class RequestLog (
    val mac: String = "",
    val uid: String = "",
    val action: String = "",
    val status: LogStatus = LogStatus.UNKNOWN,
    val timestamp: Long = 0L
)