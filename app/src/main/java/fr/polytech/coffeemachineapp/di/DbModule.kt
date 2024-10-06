package fr.polytech.coffeemachineapp.di

import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    viewModel{ DeviceViewModel() }
}