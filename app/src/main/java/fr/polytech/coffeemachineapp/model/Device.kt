package fr.polytech.coffeemachineapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Device (
    val mac: String = "",
    val name: String = "",
    val status: String = ""
) : Parcelable