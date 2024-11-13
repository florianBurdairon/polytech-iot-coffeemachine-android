package fr.polytech.coffeemachineapp.utils

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult

class BLEScanCallBack(
    val onScanResult: (scanResult: ScanResult) -> Unit,
    val onScanResultFailed: (errorCode: Int) -> Unit
): ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        onScanResult(result)
    }

    override fun onScanFailed(errorCode: Int) {
        onScanResultFailed(errorCode)
    }
}