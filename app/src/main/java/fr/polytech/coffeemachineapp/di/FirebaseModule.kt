package fr.polytech.coffeemachineapp.di

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.polytech.coffeemachineapp.repository.FirebaseRepository
import fr.polytech.coffeemachineapp.repository.FirebaseRepositoryImpl
import org.koin.dsl.module

val firebaseModule = module {
    fun provideFirebaseRepository() : FirebaseRepository {
        return FirebaseRepositoryImpl(Firebase.database)
    }

    single { provideFirebaseRepository() }
}