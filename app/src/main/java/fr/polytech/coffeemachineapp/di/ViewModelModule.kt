package fr.polytech.coffeemachineapp.di

import fr.polytech.coffeemachineapp.utils.BLEScannerViewModel
import fr.polytech.coffeemachineapp.viewmodel.AuthViewModel
import fr.polytech.coffeemachineapp.viewmodel.BluetoothViewModel
import fr.polytech.coffeemachineapp.viewmodel.DeviceViewModel
import fr.polytech.coffeemachineapp.viewmodel.OwnershipViewModel
import fr.polytech.coffeemachineapp.viewmodel.PermissionsViewModel
import fr.polytech.coffeemachineapp.viewmodel.SensorViewModel
import fr.polytech.coffeemachineapp.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel{ DeviceViewModel(get()) }
    viewModel{ SensorViewModel(get()) }
    viewModel{ BluetoothViewModel(get()) }
    viewModel{ OwnershipViewModel(get()) }
    viewModel{ PermissionsViewModel() }
    viewModel{ UserViewModel(get()) }
    viewModel{ BLEScannerViewModel(get()) }
}