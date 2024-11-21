package fr.polytech.coffeemachineapp.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

interface BLERepository {
    @RequiresPermission(value = "android. permission. BLUETOOTH_CONNECT")
    fun writeCharacteristic(device: BluetoothDevice, serviceUUID: UUID, characteristicUUID: UUID, data: String, gattCallback: BluetoothGattCallback? = null)
    @RequiresPermission(value = "android. permission. BLUETOOTH_CONNECT")
    fun readCharacteristic(device: BluetoothDevice, serviceUUID: UUID, characteristicUUID: UUID)
}

@SuppressLint("MissingPermission")
class BLERepositoryImpl(private val context: Context) : BLERepository {
    override fun writeCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: String,
        gattCallback: BluetoothGattCallback?
    ) {
        val wifiCredentialBytes = data.toByteArray()
        val deviceGatt = device.connectGatt(context, false, gattCallback)
        val wifiCredentialCharacteristic = deviceGatt.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
        if (wifiCredentialCharacteristic != null) {
            Log.d("BLEViewModel", "Sending wifi credential to BLE device")
            deviceGatt.writeCharacteristic(wifiCredentialCharacteristic, wifiCredentialBytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        }
        else {
            Log.e("BLEViewModel", "Characteristic not found")
        }
    }

    override fun readCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID
    ) {
        val deviceGatt = device.connectGatt(context, false, null)
        val characteristic = deviceGatt.getService(serviceUUID)?.getCharacteristic(characteristicUUID)
        if (characteristic != null) {
            Log.d("BLEViewModel", "Reading characteristic from BLE device")
            deviceGatt.readCharacteristic(characteristic)
        }
        else {
            Log.e("BLEViewModel", "Characteristic not found")
        }
    }

}