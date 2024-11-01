package fr.polytech.coffeemachineapp.di

import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.BluetoothViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
import fr.polytech.coffeemachineapp.viewmodel.PermissionsViewModel
import fr.polytech.coffeemachineapp.viewmodel.SensorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel{ DeviceViewModel(get()) }
    viewModel{ SensorViewModel(get()) }
    viewModel{ BluetoothViewModel(get()) }
    viewModel{ OwnershipViewModel(get()) }
    viewModel{ PermissionsViewModel() }
}