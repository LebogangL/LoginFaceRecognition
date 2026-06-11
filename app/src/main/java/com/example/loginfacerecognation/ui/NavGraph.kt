package com.example.loginfacerecognation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.ui.Modifier

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Registration : Screen("registration")
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Registration.route) }
            )
        }
        composable(Screen.Registration.route) {
            CameraScreen(
                isRegistering = true,
                onBackClick = { navController.popBackStack() },
                onSuccess = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Registration.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            CameraScreen(
                isRegistering = false,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onSignOutClick = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
