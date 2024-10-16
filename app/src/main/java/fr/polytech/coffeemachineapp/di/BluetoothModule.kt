package fr.polytech.coffeemachineapp.di

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import fr.polytech.coffeemachineapp.repository.BluetoothRepository
import fr.polytech.coffeemachineapp.repository.BluetoothRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    fun provideBluetoothManager(context: Context): BluetoothManager {
        return getSystemService(context, BluetoothManager::class.java) as BluetoothManager
    }

    fun provideBluetoothRepository(bluetoothManager: BluetoothManager, context: Context): BluetoothRepository {
        return BluetoothRepositoryImpl(bluetoothManager, context)
    }

    single { provideBluetoothManager(androidContext()) }
    single { provideBluetoothRepository(get(), androidContext()) }
}