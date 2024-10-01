package fr.polytech.coffeemachineapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import fr.polytech.coffeemachineapp.ui.destinations.DirectionDestination
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.SettingsViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.StatsViewDestination

class NavItem(val id: Int, val route: DirectionDestination, val icon: ImageVector, val label: String) {

    companion object {
        val HomeItem = NavItem(0, HomeViewDestination, Icons.Default.Home, "Home")
        val StatsItem = NavItem(1, StatsViewDestination, Icons.Default.Info, "Stats")
        val SettingsItem = NavItem(2, SettingsViewDestination, Icons.Default.Settings, "Settings")
        val items = listOf(HomeItem, StatsItem, SettingsItem)
    }
}