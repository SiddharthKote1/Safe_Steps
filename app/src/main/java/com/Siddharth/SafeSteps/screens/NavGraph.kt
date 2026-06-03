package com.Siddharth.SafeSteps.screens


import com.Siddharth.SafeSteps.screens.IntroScreen
import com.Siddharth.SafeSteps.LocationPermission

import com.Siddharth.SafeSteps.NeeScreen
import com.Siddharth.SafeSteps.screens.PermissionScreen
import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.Siddharth.SafeSteps.PreferencesHelper

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as Activity
    val preferencesHelper = PreferencesHelper(context)

    // Helper function to check if MainScreen info is filled
    fun isUserDataComplete(): Boolean {
        val user = preferencesHelper.getUserData() ?: return false
        return user.name.isNotBlank() &&
                user.phone1.isNotBlank() &&
                user.phone2.isNotBlank()
    }

    // Determine start destination is now handled by CustomSplashScreen
    val startDestination = Routes.SPLASH_SCREEN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SPLASH_SCREEN) {
            CustomSplashScreen(navController = navController)
        }
        
        composable(Routes.INTRO_SCREEN) {
            IntroScreen(navController = navController)
        }


        composable(Routes.LOGIN_SCREEN) {
            LoginScreen(navController = navController)
        }
        
        composable(Routes.REGISTER_SCREEN) {
            RegisterScreen(navController = navController)
        }

        composable(Routes.PERMISSION_SCREEN) {
            PermissionScreen(navController = navController)
        }

        composable(Routes.LOCATION_SCREEN) {
            LocationPermission(navController = navController)
        }
        composable(Routes.SETUP_CONTACTS) {
            SetupContactsScreen(navController)
        }

        composable(
            route = "NeeScreen/{name}/{countryCode1}/{countryCode2}/{phoneNumber1}/{phoneNumber2}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("countryCode1") { type = NavType.StringType },
                navArgument("countryCode2") { type = NavType.StringType },
                navArgument("phoneNumber1") { type = NavType.StringType },
                navArgument("phoneNumber2") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            NeeScreen(
                name = backStackEntry.arguments?.getString("name") ?: "",
                countryCode1 = backStackEntry.arguments?.getString("countryCode1") ?: "",
                countryCode2 = (backStackEntry.arguments?.getString("countryCode2") ?: "").let { if (it == "none") "" else it },
                phoneNumber1 = backStackEntry.arguments?.getString("phoneNumber1") ?: "",
                phoneNumber2 = (backStackEntry.arguments?.getString("phoneNumber2") ?: "").let { if (it == "none") "" else it },
                navController= navController
            )
        }
        
        composable(
            route = Routes.CHAT_SCREEN,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            ChatScreen(
                sessionId = sessionId,
                navController = navController
            )
        }
        
        composable(
            route = Routes.REPORT_SCREEN,
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
            IncidentReportScreen(
                sessionId = sessionId,
                navController = navController
            )
        }
        
        composable(Routes.HISTORY_SCREEN) {
            HistoryScreen(navController = navController)
        }
        
        composable(Routes.ANALYTICS_SCREEN) {
            AnalyticsScreen(navController = navController)
        }
        
        composable(Routes.PROFILE_SCREEN) {
            ProfileScreen(navController = navController)
        }
    }
}
