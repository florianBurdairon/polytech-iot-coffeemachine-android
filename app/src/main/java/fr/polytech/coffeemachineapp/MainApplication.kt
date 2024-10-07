package fr.polytech.coffeemachineapp

import android.app.Application
import fr.polytech.coffeemachineapp.di.authModule
import fr.polytech.coffeemachineapp.di.firebaseModule
import fr.polytech.coffeemachineapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                authModule,
                firebaseModule,
                viewModelModule
            )
        }
    }
}