package fr.polytech.coffeemachineapp.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import fr.polytech.coffeemachineapp.repository.BLERepository
import fr.polytech.coffeemachineapp.repository.BLERepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    fun provideBLERepository(context: Context): BLERepository {
        return BLERepositoryImpl(context)
    }

    fun provideBluetoothManager(context: Context): BluetoothManager? {
        return context.getSystemService(BluetoothManager::class.java)
    }

    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter? {
        return bluetoothManager?.adapter
    }

    single { provideBLERepository(androidContext()) }
    single { provideBluetoothManager(androidContext()) }
    single { provideBluetoothAdapter(get()) }
}