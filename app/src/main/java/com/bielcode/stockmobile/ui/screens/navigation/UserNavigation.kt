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
import com.bielcode.stockmobile.ui.screens.transaction.gudang.transactiondeliveryconfirm.TransactionDeliveryConfirmScreen_Delivery
import com.bielcode.stockmobile.ui.screens.transaction.transactiondetail.TransactionDetailScreen
import com.bielcode.stockmobile.ui.screens.transaction.transactiondetail.TransactionDetailScreen_Delivery
import com.bielcode.stockmobile.ui.screens.transaction.marketing.searchaddproduct.SearchAddProductScreen
import com.bielcode.stockmobile.ui.screens.transaction.marketing.transactionentry.TransactionEntryScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.BarcodeScannerScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.CheckTransactionStockInputScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.StockInputScreen
import com.bielcode.stockmobile.ui.screens.utility.barcodescanner.TransactionStockInputScreen
import com.bielcode.stockmobile.ui.screens.utility.camera.CameraScreen
import com.bielcode.stockmobile.ui.screens.utility.documentscanner.DocumentScannerScreen
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
            composable("barcodeScanner/{catalog}/{itemSize}",
                arguments = listOf(
                    navArgument("catalog"){ type = NavType.StringType },
                    navArgument("itemSize") { type = NavType.StringType },
                )
            ) {backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val itemSize = backStackEntry.arguments?.getString("itemSize") ?: ""
                BarcodeScannerScreen(navController, catalog, itemSize, 0, "")
            }
            composable(
                "barcodeScanner/{catalog}/{itemSize}/{itemQty}/{transactionCode}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType },
                    navArgument("itemSize") { type = NavType.StringType },
                    navArgument("itemQty") { type = NavType.IntType },
                    navArgument("transactionCode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val itemSize = backStackEntry.arguments?.getString("itemSize") ?: ""
                val itemQty = backStackEntry.arguments?.getInt("itemQty") ?: 0
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                BarcodeScannerScreen(navController, catalog, itemSize, itemQty, transactionCode)
            }
            composable(
                "checkTransactionStockInput/{catalog}/{initSize}/{supposedQty}/{transactionCode}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType },
                    navArgument("initSize") { type = NavType.StringType },
                    navArgument("supposedQty") { type = NavType.IntType },
                    navArgument("transactionCode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val initSize = backStackEntry.arguments?.getString("initSize") ?: ""
                val supposedQty = backStackEntry.arguments?.getInt("supposedQty") ?: 0
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                CheckTransactionStockInputScreen(
                    catalog = catalog,
                    initSize = initSize,
                    supposedQty = supposedQty,
                    transactionCode = transactionCode,
                    navController = navController
                )
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
                TransactionEntryScreen(navController)
            }
            
            composable(Screen.SearchAddProduct.route){
                SearchAddProductScreen(navController = navController)
            }

            composable(
                route = "${Screen.TransactionStockInput.route}/{catalog}/{initSize}",
                arguments = listOf(
                    navArgument("catalog") { type = NavType.StringType },
                    navArgument("initSize") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val catalog = backStackEntry.arguments?.getString("catalog") ?: ""
                val initSize = backStackEntry.arguments?.getString("initSize") ?: ""
                TransactionStockInputScreen(navController, catalog, initSize)
            }
            composable(
                "documentScanner/{transactionCode}?isForRead={isForRead}",
                arguments = listOf(
                    navArgument("transactionCode") { type = NavType.StringType },
                    navArgument("isForRead") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                val isForRead = backStackEntry.arguments?.getBoolean("isForRead") ?: false
                DocumentScannerScreen(navController, transactionCode, isForRead)
            }
            composable(
                route = "${Screen.TransactionDetail.route}/{transactionCode}",
                arguments = listOf(navArgument("transactionCode") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                TransactionDetailScreen(navController, transactionCode)
            }

            composable(
                route = "${Screen.TransactionDetail_Delivery.route}/{transactionCode}",
                arguments = listOf(navArgument("transactionCode") { type = NavType.StringType })
            ) { backStackEntry ->
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                TransactionDetailScreen_Delivery(navController, transactionCode)
            }
            composable(
                route = "transactionEntry/{transactionCode}",
                arguments = listOf(
                    navArgument("transactionCode") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val transactionCode = backStackEntry.arguments?.getString("transactionCode")
                TransactionEntryScreen(navController, transactionCode)
            }
            composable(
                route = "${Screen.TransactionDeliveryConfirm.route}/{transactionCode}",
                arguments = listOf(
                    navArgument("transactionCode") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val transactionCode = backStackEntry.arguments?.getString("transactionCode") ?: ""
                TransactionDeliveryConfirmScreen_Delivery(navController, transactionCode)
            }

        }
    }
}

