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

    fun provideBluetoothAdapter(context: Context): BluetoothAdapter? {
        val bluetoothManager by lazy {
            context.getSystemService(BluetoothManager::class.java)
        }
        val bluetoothAdapter by lazy {
            bluetoothManager?.adapter
        }
        return bluetoothAdapter
    }

    single { provideBluetoothRepository(androidContext()) }
    single { provideBluetoothAdapter(androidContext()) }
}