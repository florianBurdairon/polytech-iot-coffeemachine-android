package fr.polytech.coffeemachineapp.model

import android.os.Parcelable
import fr.polytech.coffeemachineapp.utils.DeviceStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device (
    val mac: String = "",
    val name: String = "",
    val status: DeviceStatus = DeviceStatus.UNKNOWN,
    val lastOnline: Long = 0L
) : Parcelable