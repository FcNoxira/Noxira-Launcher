// NOXIRA — Premium Roblox Launcher
// File: NoxiraApp.kt
// Navigation host + app entry composable

package com.noxira.launcher

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noxira.launcher.ui.screens.*
import com.noxira.launcher.ui.theme.NoxiraTheme

object Routes {
    const val SPLASH     = "splash"
    const val DASHBOARD  = "dashboard"
    const val WINDOW_MGR = "window_manager"
    const val SETTINGS   = "settings"
    const val DIAGNOSTICS= "diagnostics"
    const val ABOUT      = "about"
}

@Composable
fun NoxiraApp() {
    NoxiraTheme {
        val navController = rememberNavController()
        val vm: MainViewModel = viewModel()

        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            enterTransition  = { fadeIn(tween(200)) },
            exitTransition   = { fadeOut(tween(150)) },
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(onReady = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    vm = vm,
                    onNavigateSettings  = { navController.navigate(Routes.SETTINGS) },
                    onNavigateDiag      = { navController.navigate(Routes.DIAGNOSTICS) },
                    onNavigateWindowMgr = { navController.navigate(Routes.WINDOW_MGR) },
                )
            }

            composable(Routes.WINDOW_MGR) {
                WindowManagerScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    vm = vm,
                    onBack  = { navController.popBackStack() },
                    onAbout = { navController.navigate(Routes.ABOUT) },
                )
            }

            composable(Routes.DIAGNOSTICS) {
                DiagnosticsScreen(
                    vm = vm,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(Routes.ABOUT) {
                AboutScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
