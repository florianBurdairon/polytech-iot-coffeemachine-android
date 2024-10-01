package fr.polytech.coffeemachineapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import fr.polytech.coffeemachineapp.ui.NavGraphs
import fr.polytech.coffeemachineapp.ui.components.NavItem
import fr.polytech.coffeemachineapp.ui.components.NavItem.Companion.items
import fr.polytech.coffeemachineapp.ui.theme.CoffeeMachineAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val snackBarScope = rememberCoroutineScope()
            val navController = rememberNavController()
            var selectedItem by rememberSaveable { mutableIntStateOf(NavItem.HomeItem.id) } // TODO: move to viewmodel
            val currentDestination: DestinationSpec<*> = navController.currentDestinationAsState().value
                ?: NavGraphs.root.startDestination

            CoffeeMachineAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    bottomBar = {
                        NavigationBar {
                            Log.d("BottomNavItem", "first item: ${items.first()}")
                            items.forEach { item ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            item.icon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = { Text(item.label) },
                                    selected = currentDestination == item.route,
                                    onClick = {
                                        selectedItem = item.id
                                        navController.navigate(item.route.route)
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        dependenciesContainerBuilder = {
                            dependency(snackbarHostState)
                            dependency(snackBarScope)
                        }
                    )
                }
            }
        }
    }
}