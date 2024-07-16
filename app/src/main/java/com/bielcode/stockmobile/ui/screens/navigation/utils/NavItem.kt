package com.bielcode.stockmobile.ui.screens.navigation.utils

import androidx.compose.ui.graphics.vector.ImageVector

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val iconSelect: ImageVector,
    val screen: Screen,
)