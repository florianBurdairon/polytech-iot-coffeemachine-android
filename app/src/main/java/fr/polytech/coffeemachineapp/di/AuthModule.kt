package fr.polytech.coffeemachineapp.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val authModule = module {
    fun provideFirebaseAuth() : FirebaseAuth {
        return Firebase.auth
    }
    single { provideFirebaseAuth() }
}