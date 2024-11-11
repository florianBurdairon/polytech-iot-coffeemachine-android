package fr.polytech.coffeemachineapp.repository

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import fr.polytech.coffeemachineapp.viewmodel.FoundDeviceReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

interface BluetoothRepository {
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>

    fun startDiscovery()
    fun stopDiscovery()

    fun connect(device: BluetoothDevice)
    fun disconnect()
    fun write(message: String)
    fun startListeningForData(): LiveData<String>
}

@SuppressLint("MissingPermission")
class BluetoothRepositoryImpl(private val context: Context) : BluetoothRepository {
    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

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

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver { device ->
        _scannedDevices.update { devices -> if(device in devices || device.name == null) devices else devices + device }
    }

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

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        if (!hasPermissions(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(BluetoothDevice.ACTION_FOUND)
        )
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermissions(Manifest.permission.BLUETOOTH_SCAN)) {
            return
        }
        bluetoothAdapter?.cancelDiscovery()
        context.unregisterReceiver(foundDeviceReceiver)
    }

    override fun connect(device: BluetoothDevice) {
        // Connect to the Bluetooth device
        if (hasPermissions(Manifest.permission.BLUETOOTH_CONNECT)) {
            _connectionState.value = ConnectionState.Connecting

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID))
                bluetoothSocket?.connect()

                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream

                _connectionState.value = ConnectionState.Connected
                Log.d("BluetoothRepository", "Connected to ${device.name}")

                // Start a thread to listen for incoming data
                startListeningForData()
            } catch (e: IOException) {
                _connectionState.value = ConnectionState.Disconnected
                Log.e("BluetoothRepository", "Error connecting to ${device.name}: ${e.message}")
                // Handle connection error
            } catch (e: SecurityException) {
                _connectionState.value = ConnectionState.Disconnected
                Log.e("BluetoothRepository", "Permission denied: ${e.message}")
                // Handle permission denied error
            }
        } else {
            // Request the permission
            // You should handle this in your Activity or Fragment
            // using ActivityCompat.requestPermissions() or similar methods
            _connectionState.value = ConnectionState.Disconnected
            Log.e("BluetoothRepository", "Permission not granted")
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

    override fun startListeningForData(): LiveData<String> {
        isListeningForData = true
        bluetoothListenerThread.start()
        return receivedData

    }

    override fun write(message: String) {
        // Send data to the Bluetooth device
        try {
            outputStream?.write(message.toByteArray())
        } catch (e: IOException) {
            // Handle send error
        }
    }

    private fun updatePairedDevices() {
        if (!hasPermissions(Manifest.permission.BLUETOOTH_CONNECT)) {
            return
        }
        bluetoothAdapter?.bondedDevices?.let { _pairedDevices.update { it.toList() } }
    }

    private fun hasPermissions(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}