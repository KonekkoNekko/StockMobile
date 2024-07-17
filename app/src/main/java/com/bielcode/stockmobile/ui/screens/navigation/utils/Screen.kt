package com.bielcode.stockmobile.ui.screens.navigation.utils

sealed class Screen(
    val route: String,
){
    // Authentication Route
    data object Welcome: Screen("welcome")
    data object Login: Screen("login")
    data object Register: Screen("register")

    // Bottom Navigation Route
    data object Home: Screen("home")
    data object Stock: Screen("stock")
    data object Transaction: Screen("transaction")
    data object Partner: Screen("partner")

    //Stock Screen Route
    data object StockDetail: Screen("stockDetail")
    data object StockEntry: Screen("stockEntry")

    data object Camera : Screen("cameraScreen")

    // Partner Screen Route
    data object PartnerDetail : Screen("partnerDetail")
    data object PartnerEntry : Screen("partnerEntry")
    data object ContactEntry : Screen("contactEntry")

    // Transaction Screen Route
    data object TransactionDetail : Screen("transactionDetail")
    data object TransactionEntry : Screen("transactionEntry")
    data object SearchAddProduct : Screen("searchAddProduct")
    data object TransactionStockInput : Screen("transactionStockInput")

}

