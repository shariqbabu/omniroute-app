package com.omniroute.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.omniroute.app.ui.screens.dashboard.DashboardScreen
import com.omniroute.app.ui.screens.logs.LogsScreen
import com.omniroute.app.ui.screens.playground.PlaygroundScreen
import com.omniroute.app.ui.screens.providers.ProvidersScreen
import com.omniroute.app.ui.screens.routing.RoutingScreen
import com.omniroute.app.ui.screens.settings.SettingsScreen
import com.omniroute.app.ui.viewmodel.MainViewModel

@Composable
fun OmniNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToPlayground = { navController.navigate(Screen.Playground.route) },
                onNavigateToProviders = { navController.navigate(Screen.Providers.route) },
                onNavigateToRouting = { navController.navigate(Screen.Routing.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Playground.route) {
            PlaygroundScreen(viewModel = viewModel)
        }
        composable(Screen.Providers.route) {
            ProvidersScreen(viewModel = viewModel)
        }
        composable(Screen.Routing.route) {
            RoutingScreen(viewModel = viewModel)
        }
        composable(Screen.Logs.route) {
            LogsScreen(viewModel = viewModel)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}
