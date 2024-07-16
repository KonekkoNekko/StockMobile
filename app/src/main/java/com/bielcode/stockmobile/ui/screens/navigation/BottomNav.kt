package com.bielcode.stockmobile.ui.screens.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bielcode.stockmobile.ui.screens.navigation.utils.NavItem
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = Modifier,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        val navigationItems = listOf(
            NavItem(
                title = "Home",
                icon = Icons.Outlined.Home,
                iconSelect = Icons.Filled.Home,
                screen = Screen.Home
            ),
            NavItem(
                title = "Stok",
                icon = Icons.Outlined.Inventory,
                iconSelect = Icons.Filled.Inventory,
                screen = Screen.Stock
            ),
            NavItem(
                title = "Transaction",
                icon = Icons.Outlined.SwapHoriz,
                iconSelect = Icons.Filled.SwapHoriz,
                screen = Screen.Transaction
            ),
            NavItem(
                title = "Mitra",
                icon = Icons.Outlined.People,
                iconSelect = Icons.Filled.People,
                screen = Screen.Partner
            ),
        )

        navigationItems.map { item ->
            NavigationBarItem(
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                icon = {
                    if (currentRoute == item.screen.route && item.iconSelect != null) {
                        Icon(imageVector = item.iconSelect, contentDescription = item.title)
                    } else {
                        Icon(imageVector = item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) }
            )
        }
    }
}