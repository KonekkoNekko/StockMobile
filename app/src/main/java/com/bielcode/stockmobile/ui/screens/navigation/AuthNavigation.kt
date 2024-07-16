package com.bielcode.stockmobile.ui.screens.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bielcode.stockmobile.ui.screens.authentication.register.RegisterScreen
import com.bielcode.stockmobile.ui.screens.authentication.WelcomeScreen
import com.bielcode.stockmobile.ui.screens.authentication.login.LoginScreen
import com.bielcode.stockmobile.ui.screens.authentication.login.LoginViewModel
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen

@Composable
fun AuthNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.White
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                WelcomeScreen(toLogin = {
                    navController.navigate(Screen.Login.route)
                })
            }
            composable(Screen.Login.route) {
                LoginScreen(toRegister = {
                    navController.navigate(Screen.Register.route)
                })
            }
            composable(Screen.Register.route) {
                RegisterScreen(toLogin = {
                    navController.navigate(Screen.Login.route)
                })
            }
        }
    }
}