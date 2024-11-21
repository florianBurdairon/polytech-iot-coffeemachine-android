package fr.polytech.coffeemachineapp.viewmodel

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.polytech.coffeemachineapp.model.WifiData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WifiViewModel(val wifiManager: WifiManager) : ViewModel() {
    private val _wifiList = MutableStateFlow<List<WifiData>>(emptyList())
    val wifiList: StateFlow<List<WifiData>> = _wifiList.asStateFlow()


    val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    fun startScan(context: Context) {
        val intentFilter = IntentFilter()

        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)

        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanSuccess() {
        val results = wifiManager.scanResults
        val wifiResults = results.map {
            if (it.SSID.isNotEmpty() && _wifiList.value.none { existing -> existing.bssid == it.BSSID }) {
                WifiData(it.SSID, it.BSSID, it.level, it.frequency, it.capabilities)
            }
            else {
                null
            }
        }.filterNotNull()
        viewModelScope.launch {
            _wifiList.update { _wifiList.value + wifiResults }
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        val results = wifiManager.scanResults
    }
}