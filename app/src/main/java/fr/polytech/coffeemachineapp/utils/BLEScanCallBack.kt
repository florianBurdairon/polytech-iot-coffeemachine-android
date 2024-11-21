package fr.polytech.coffeemachineapp.utils

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.ParcelUuid
import android.util.Log
import fr.polytech.coffeemachineapp.utils.Constant.Companion.SERVICE_UUID

class BLEScanCallBack(
    private val serviceUUID: String? = SERVICE_UUID,
    val onScanResult: (scanResult: ScanResult) -> Unit,
    val onScanResultFailed: (errorCode: Int) -> Unit
): ScanCallback() {
    @SuppressLint("MissingPermission")
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        val scanRecord = result.scanRecord
        if (scanRecord != null) {
            val serviceUuids = scanRecord.serviceUuids
            if (serviceUuids != null && (serviceUUID == null || serviceUuids.contains(ParcelUuid.fromString(serviceUUID)))) {
                onScanResult(result)
            }
            else {
                Log.d("ScanCallback", "Service UUID not found")
            }
        }
        else {
            Log.d("ScanCallback", "Scan record is null")
        }
    }

    override fun onScanFailed(errorCode: Int) {
        onScanResultFailed(errorCode)
    }
}