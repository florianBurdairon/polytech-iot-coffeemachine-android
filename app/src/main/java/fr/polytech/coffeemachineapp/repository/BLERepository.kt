package fr.polytech.coffeemachineapp.repository

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

interface BLERepository {
    @RequiresPermission(value = "android. permission. BLUETOOTH_CONNECT")
    fun writeCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: String,
        onWriteSuccess: () -> Unit = {},
        onWriteFailure: () -> Unit = {}
    )
    @RequiresPermission(value = "android. permission. BLUETOOTH_CONNECT")
    fun readCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        onReadSuccess: (String) -> Unit,
        onReadFailure: () -> Unit = {}
    )
}

class BLERepositoryImpl(private val context: Context) : BLERepository {
    @SuppressLint("NewApi", "MissingPermission")
    override fun writeCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        data: String,
        onWriteSuccess: () -> Unit,
        onWriteFailure: () -> Unit
    ) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices() // Start service discovery
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BLEViewModel", "Disconnected from BLE device")
                    onWriteFailure()
                    gatt.close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val service = gatt.getService(serviceUUID)
                    val characteristic = service?.getCharacteristic(characteristicUUID)
                    if (service == null) {
                        Log.e("BLEViewModel", "Service not found")
                        onWriteFailure()
                        gatt.close()
                    } else if (characteristic != null) {
                        Log.d("BLEViewModel", "Sending wifi credential to BLE device")
                        gatt.writeCharacteristic(characteristic, data.toByteArray(), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                    } else {
                        Log.e("BLEViewModel", "Characteristic not found")
                        onWriteFailure()
                        gatt.close()
                    }
                }
            }

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BLEViewModel", "Write data to BLE device successfully")
                    onWriteSuccess()
                    gatt?.close()
                }
                else {
                    Log.e("BLEViewModel", "Failed to write data to BLE device")
                    onWriteFailure()
                    gatt?.close()
                }
            }
        }
        device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    override fun readCharacteristic(
        device: BluetoothDevice,
        serviceUUID: UUID,
        characteristicUUID: UUID,
        onReadSuccess: (String) -> Unit,
        onReadFailure: () -> Unit
    ) {
        val gattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices() // Start service discovery
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d("BLEViewModel", "Disconnected from BLE device")
                    onReadFailure()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val service = gatt.getService(serviceUUID)
                    val characteristic = service?.getCharacteristic(characteristicUUID)
                    if (service == null) {
                        Log.e("BLEViewModel", "Service not found")
                        onReadFailure()
                    } else if (characteristic != null) {
                        gatt.readCharacteristic(characteristic)
                    } else {
                        Log.e("BLEViewModel", "Characteristic not found")
                        onReadFailure()
                    }
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    val data = String(value)
                    Log.d("BLEViewModel", "Read data from BLE device: $data")
                    onReadSuccess(data)
                }
                else {
                    Log.e("BLEViewModel", "Failed to read data from BLE device")
                    onReadFailure()
                }
            }
        }
        device.connectGatt(context, false, gattCallback)
    }

}