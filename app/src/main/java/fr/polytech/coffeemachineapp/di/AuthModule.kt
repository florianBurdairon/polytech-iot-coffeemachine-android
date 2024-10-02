package fr.polytech.coffeemachineapp.di


import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { AuthViewModel() }
}