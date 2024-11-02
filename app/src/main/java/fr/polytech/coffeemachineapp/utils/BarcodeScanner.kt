package fr.polytech.coffeemachineapp.utils

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.tasks.await

class BarcodeScanner(context: Context) {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = GmsBarcodeScanning.getClient(context, options)

    suspend fun startScan(): String? {
        return try {
            scanner.startScan().await()?.rawValue
        }
        catch (e: Exception) {
            Log.e("BarcodeScanner", "Error starting scan", e)
            null
        }
    }
}