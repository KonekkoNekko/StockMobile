package com.bielcode.stockmobile.ui.screens.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bielcode.stockmobile.ui.screens.home.HomeScreen
import com.bielcode.stockmobile.ui.screens.partner.PartnerScreen
import com.bielcode.stockmobile.ui.screens.stock.StockScreen
import com.bielcode.stockmobile.ui.screens.transaction.TransactionScreen
import com.bielcode.stockmobile.ui.screens.navigation.utils.Screen
import com.bielcode.stockmobile.ui.screens.partner.contactentry.ContactEntryScreen
import com.bielcode.stockmobile.ui.screens.partner.detail.PartnerDetailScreen
import com.bielcode.stockmobile.ui.screens.partner.entry.PartnerEntryScreen
import com.bielcode.stockmobile.ui.screens.stock.detail.StockDetailScreen
import com.bielcode.stockmobile.ui.screens.stock.entry.StockEntryScreen
import com.bielcode.stockmobile.ui.screens.transaction.marketing.transactionentry.TransactionEntryScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.BarcodeScannerScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.StockInputScreen
import com.bielcode.stockmobile.ui.screens.utility.camera.CameraScreen
import com.bielcode.stockmobile.ui.screens.utility.mapspicker.MapsPickerScreen

@Composable
fun UserNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    role: String,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = Color.White,
        bottomBar = {
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.Stock.route ||
                currentRoute == Screen.Transaction.route ||
                currentRoute == Screen.Partner.route
            ) {
                BottomNavBar(navController = navController)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(it)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Stock.route) {
                StockScreen(navController)
            }
            composable(Screen.Transaction.route) {
                TransactionScreen(navController)
            }
            composable(Screen.Partner.route) {
                PartnerScreen(navController)
            }
            composable(
                "${Screen.StockDetail.route}/{catalog}/{size}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType },
                    navArgument("size") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val size = backStackEntry.arguments?.getString("size") ?: ""
                StockDetailScreen(
                    catalog = catalog,
                    initSize = size,
                    navController = navController
                )
            }
            composable("barcodeScanner") {
                BarcodeScannerScreen(navController)
            }
            composable(
                "stockInput/{catalog}/{initSize}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType },
                    navArgument("initSize") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val initSize = backStackEntry.arguments?.getString("initSize") ?: ""
                StockInputScreen(
                    navController = navController,
                    catalog = catalog,
                    initSize = initSize
                )
            }
            composable(Screen.StockEntry.route) {
                StockEntryScreen(navController)
            }

            composable(
                "${Screen.StockEntry.route}?catalog={catalog}",
                arguments = listOf(
                    navArgument("catalog") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog")
                StockEntryScreen(navController, catalog)
            }

            composable(
                route = "cameraScreen/{folder}/{filename}",
                arguments = listOf(
                    navArgument("folder") { type = NavType.StringType },
                    navArgument("filename") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val folder = backStackEntry.arguments?.getString("folder") ?: ""
                val filename = backStackEntry.arguments?.getString("filename") ?: ""
                CameraScreen(navController = navController, folder = folder, filename = filename)
            }

            composable(Screen.PartnerEntry.route) {
                PartnerEntryScreen(navController)
            }

            composable(
                "${Screen.PartnerEntry.route}?name={name}",
                arguments = listOf(
                    navArgument("name") {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name")
                PartnerEntryScreen(navController, name)
            }

            composable(
                "${Screen.PartnerDetail.route}/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                PartnerDetailScreen(
                    name = name,
                    navController = navController
                )
            }

            composable(
                "${Screen.ContactEntry.route}?partnerName={partnerName}&contactName={contactName}&contactPosition={contactPosition}&contactPhone={contactPhone}",
                arguments = listOf(
                    navArgument("partnerName") { type = NavType.StringType },
                    navArgument("contactName") { type = NavType.StringType; nullable = true },
                    navArgument("contactPosition") { type = NavType.StringType; nullable = true },
                    navArgument("contactPhone") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val partnerName = backStackEntry.arguments?.getString("partnerName") ?: ""
                val contactName = backStackEntry.arguments?.getString("contactName")
                val contactPosition = backStackEntry.arguments?.getString("contactPosition")
                val contactPhone = backStackEntry.arguments?.getString("contactPhone")
                ContactEntryScreen(
                    partnerName = partnerName,
                    contactName = contactName,
                    contactPosition = contactPosition,
                    contactPhone = contactPhone,
                    onSave = { navController.popBackStack() },
                    onDelete = { navController.popBackStack() },
                    navController = navController
                )
            }

            composable("mapsPickerScreen") {
                MapsPickerScreen(navController = navController)
            }

            // Transaction
            composable(Screen.TransactionEntry.route){
                TransactionEntryScreen()
            }
        }
    }
}

