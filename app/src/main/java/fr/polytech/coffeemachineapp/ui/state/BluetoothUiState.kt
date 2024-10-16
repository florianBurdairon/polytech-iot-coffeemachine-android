package fr.polytech.coffeemachineapp.ui.state

import android.bluetooth.BluetoothDevice

data class BluetoothUiState (
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList()
)