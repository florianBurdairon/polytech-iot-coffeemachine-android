package fr.polytech.coffeemachineapp.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import fr.polytech.coffeemachineapp.model.WifiCredential
import fr.polytech.coffeemachineapp.repository.BLERepository
import fr.polytech.coffeemachineapp.utils.BLEScanCallBack
import fr.polytech.coffeemachineapp.utils.Constant.Companion.DEVICE_SETUP_UUID
import fr.polytech.coffeemachineapp.utils.Constant.Companion.SERVICE_UUID
import fr.polytech.coffeemachineapp.utils.Constant.Companion.WIFI_CREDENTIAL_UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


class BLEViewModel(
    private val bluetoothAdapter: BluetoothAdapter?,
    private val bleRepository: BLERepository
) : ViewModel() {
    private var _scanResults: MutableStateFlow<List<ScanResult>> = MutableStateFlow(listOf())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()
    private var _isScanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val scanSettings: ScanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
        .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
        .build()

    @SuppressLint("MissingPermission")
    val scanCallback = BLEScanCallBack(
        serviceUUID = SERVICE_UUID,
        onScanResult = { result ->
            viewModelScope.launch {
                if (result.device.name != null) {
                    val mutableList = scanResults.value.toMutableList()
                    val indexQuery =
                        scanResults.value.indexOfFirst { it.device.address == result.device.address }
                    if (indexQuery != -1) { // A scan result already exists with the same address
                        mutableList[indexQuery] = result
                    } else {
                        with(result.device) {
                            Log.d(
                                "ScanCallback",
                                "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address"
                            )
                        }
                        mutableList.add(result)
                    }
                    _scanResults.update { mutableList.toList() }
                }
            }
        },
        onScanResultFailed = { errorCode ->
            Log.e("ScanCallback", "BLE Scan failed with error code: $errorCode")
        }
    )

    @SuppressLint("MissingPermission")
    fun startScan() {
        Log.d("BLEScanner","Starting Scan...")
        _scanResults.update { listOf() }
        _isScanning.update { true }
        bluetoothLeScanner?.startScan(
            null,
            scanSettings,
            scanCallback
        )
        viewModelScope.launch {
            delay(30000) // Delay for 30 seconds
            stopScan()
            _isScanning.update { false }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        viewModelScope.launch {
            bluetoothLeScanner?.stopScan(scanCallback)
            Log.d("BLEScanner", "Stopping Scan...")
        }
    }

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    @SuppressLint("MissingPermission")
    fun sendDeviceSetup(device: BluetoothDevice, ssid: String, password: String, onWriteSuccess: () -> Unit, onWriteFailure: () -> Unit) {
        val serviceUUID = UUID.fromString(SERVICE_UUID)
        val characteristicUUID = UUID.fromString(WIFI_CREDENTIAL_UUID)
        val wifiCredential = WifiCredential(ssid, password)
        val wifiCredentialString = Gson().toJson(wifiCredential)

        Log.d("BLEViewModel", "Wifi credential: $wifiCredentialString")

        // Send the wifi credential to the BLE device
        bleRepository.writeCharacteristic(
            device,
            serviceUUID,
            characteristicUUID,
            wifiCredentialString,
            onWriteSuccess,
            onWriteFailure
        )
    }

    @SuppressLint("MissingPermission")
    fun readDeviceSetup(device: BluetoothDevice, onReadSuccess: (String) -> Unit, onReadFailure: () -> Unit) {
        val serviceUUID = UUID.fromString(SERVICE_UUID)
        val characteristicUUID = UUID.fromString(DEVICE_SETUP_UUID)
        bleRepository.readCharacteristic(
            device,
            serviceUUID,
            characteristicUUID,
            onReadSuccess,
            onReadFailure
        )
    }
}