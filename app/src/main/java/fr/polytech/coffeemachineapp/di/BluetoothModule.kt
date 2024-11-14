package fr.polytech.coffeemachineapp.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import fr.polytech.coffeemachineapp.repository.BluetoothRepository
import fr.polytech.coffeemachineapp.repository.BluetoothRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    fun provideBluetoothRepository(context: Context): BluetoothRepository {
        return BluetoothRepositoryImpl(context)
    }

    fun provideBluetoothManager(context: Context): BluetoothManager? {
        return context.getSystemService(BluetoothManager::class.java)
    }

    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter? {
        return bluetoothManager?.adapter
    }

    single { provideBluetoothRepository(androidContext()) }
    single { provideBluetoothManager(androidContext()) }
    single { provideBluetoothAdapter(get()) }
}