package fr.polytech.coffeemachineapp.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.polytech.coffeemachineapp.utils.BLEScanCallBack
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class BLEViewModel(
    private val bluetoothAdapter: BluetoothAdapter?
) : ViewModel() {
    private var _scanResults: MutableStateFlow<List<ScanResult>> = MutableStateFlow(listOf())
    val scanResults: StateFlow<List<ScanResult>> = _scanResults.asStateFlow()
    private var _isScanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner

    private val scanSettings: ScanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
        .build()

    @SuppressLint("MissingPermission")
    val scanCallback = BLEScanCallBack(
        serviceUUID = "12345678-1234-1234-1234-123456789abc",
        onScanResult = { result ->
            val mutableList = scanResults.value.toMutableList()
            val indexQuery = scanResults.value.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                mutableList[indexQuery] = result
            } else {
                with(result.device) {
                    Log.d("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address")
                }
                mutableList.add(result)
            }
            viewModelScope.launch {
                _scanResults.update { mutableList.toList() }
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
        bluetoothLeScanner?.stopScan(scanCallback)
        Log.d("BLEScanner","Stopping Scan...")
    }

    fun isBluetoothAvailable(): Boolean {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }
}