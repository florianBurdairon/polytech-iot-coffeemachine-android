package fr.polytech.coffeemachineapp.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

interface BluetoothRepository {
    fun connect(device: BluetoothDevice)
    fun disconnect()
    fun send(data: ByteArray)
    fun startListeningForData() : LiveData<String>
    fun getAvailableDevices(): List<BluetoothDevice>
}

class BluetoothRepositoryImpl(
    bluetoothManager: BluetoothManager,
    private val context: Context
) : BluetoothRepository {
    private var bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private var isListeningForData = false

    // Connection state
    private val _connectionState = MutableLiveData(ConnectionState.Disconnected)
    val connectionState: LiveData<ConnectionState> = _connectionState

    // Received data
    private val _receivedData = MutableLiveData<String>()
    val receivedData: LiveData<String> = _receivedData

    private val MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"

    private val bluetoothListenerThread = Thread {
        while (isListeningForData) {
            try {
                val bytes = ByteArray(1024)
                val bytesRead = inputStream?.read(bytes)

                if (bytesRead != null && bytesRead > 0) {
                    val receivedString = String(bytes, 0, bytesRead)
                    _receivedData.postValue(receivedString)
                }
            } catch (e: IOException) {
                // Handle receive error
                break
            }
        }
    }

    enum class ConnectionState {
        Disconnected, Connecting, Connected
    }

    override fun connect(device: BluetoothDevice) {
        // Connect to the Bluetooth device
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            _connectionState.value = ConnectionState.Connecting


            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID))
                bluetoothSocket?.connect()

                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream

                _connectionState.value = ConnectionState.Connected

                // Start a thread to listen for incoming data
                startListeningForData()
            } catch (e: IOException) {
                _connectionState.value = ConnectionState.Disconnected
                // Handle connection error
            } catch (e: SecurityException) {
                _connectionState.value = ConnectionState.Disconnected
                // Handle permission denied error
            }
        } else {
            // Request the permission
            // You should handle this in your Activity or Fragment
            // using ActivityCompat.requestPermissions() or similar methods
            _connectionState.value = ConnectionState.Disconnected
            // Handle permission not granted error
        }
    }

    override fun disconnect() {
        // Disconnect from the Bluetooth device
        try {
            bluetoothSocket?.close()
            inputStream?.close()
            outputStream?.close()

            isListeningForData = false
            _connectionState.value = ConnectionState.Disconnected
        } catch (e: IOException) {
            // Handle disconnection error
        }
    }

    override fun send(data: ByteArray) {
        // Send data to the Bluetooth device
        fun sendData(data: String) {
            try {
                outputStream?.write(data.toByteArray())
            } catch (e: IOException) {
                // Handle send error
            }
        }
    }

    override fun startListeningForData() : LiveData<String> {
        isListeningForData = true
        bluetoothListenerThread.start()
        return receivedData
    }

    override fun getAvailableDevices(): List<BluetoothDevice> {
        return bluetoothAdapter?.bondedDevices?.toList() ?: emptyList()
    }
}