package fr.polytech.coffeemachineapp.di

import android.content.Context
import fr.polytech.coffeemachineapp.repository.BluetoothRepository
import fr.polytech.coffeemachineapp.repository.BluetoothRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val bluetoothModule = module {
    fun provideBluetoothRepository(context: Context): BluetoothRepository {
        return BluetoothRepositoryImpl(context)
    }

    single { provideBluetoothRepository(androidContext()) }
}