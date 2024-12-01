package fr.polytech.coffeemachineapp.ui.components

import fr.polytech.coffeemachineapp.R
import fr.polytech.coffeemachineapp.ui.destinations.DirectionDestination
import fr.polytech.coffeemachineapp.ui.destinations.HomeViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.SettingsViewDestination
import fr.polytech.coffeemachineapp.ui.destinations.StatsViewDestination

class NavItem(val id: Int, val route: DirectionDestination, val icon: Int, val label: String) {

    companion object {
        val HomeItem = NavItem(0, HomeViewDestination, R.drawable.home_24dp, "Home")
        private val StatsItem = NavItem(1, StatsViewDestination, R.drawable.bar_chart_24dp, "Stats")
        private val SettingsItem = NavItem(2, SettingsViewDestination, R.drawable.settings_24dp, "Settings")
        val items = listOf(HomeItem, StatsItem, SettingsItem)
    }
}