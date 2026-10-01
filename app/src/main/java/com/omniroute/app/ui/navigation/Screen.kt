package com.omniroute.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : Screen(
        route = "dashboard",
        title = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    object Playground : Screen(
        route = "playground",
        title = "Playground",
        selectedIcon = Icons.Filled.ChatBubble,
        unselectedIcon = Icons.Outlined.ChatBubbleOutline
    )

    object Providers : Screen(
        route = "providers",
        title = "Providers",
        selectedIcon = Icons.Filled.Hub,
        unselectedIcon = Icons.Outlined.Hub
    )

    object Routing : Screen(
        route = "routing",
        title = "Routing",
        selectedIcon = Icons.Filled.AltRoute,
        unselectedIcon = Icons.Outlined.AltRoute
    )

    object Logs : Screen(
        route = "logs",
        title = "Logs",
        selectedIcon = Icons.Filled.ReceiptLong,
        unselectedIcon = Icons.Outlined.ReceiptLong
    )

    object Settings : Screen(
        route = "settings",
        title = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Playground,
    Screen.Providers,
    Screen.Routing,
    Screen.Logs,
    Screen.Settings
)
