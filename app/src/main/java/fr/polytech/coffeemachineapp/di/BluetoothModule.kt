package fr.polytech.coffeemachineapp.di

import android.content.Context
import fr.polytech.coffeemachineapp.repository.BluetoothController
import fr.polytech.coffeemachineapp.repository.BluetoothControllerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    fun provideBluetoothRepository(context: Context): BluetoothController {
        return BluetoothControllerImpl(context)
    }

    single { provideBluetoothRepository(androidContext()) }
}