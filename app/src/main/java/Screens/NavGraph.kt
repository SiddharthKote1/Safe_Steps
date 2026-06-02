package com.Siddharth.chat


import Screens.IntroScreen
import Screens.LocationPermission
import Screens.MainScreen
import Screens.NeeScreen
import Screens.PermissionScreen
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

    // Determine start destination
    val startDestination = when {
        !preferencesHelper.isAppSetupDone() -> Routes.INTRO_SCREEN
        !isUserDataComplete() -> Routes.MAIN_SCREEN
        else -> {
            val user = preferencesHelper.getUserData()!!
            "NeeScreen/${Uri.encode(user.name)}/${Uri.encode(user.countryCode1)}/${Uri.encode(user.countryCode2)}/${Uri.encode(user.phone1)}/${Uri.encode(user.phone2)}"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.INTRO_SCREEN) {
            IntroScreen(navController = navController)
        }


        composable(Routes.PERMISSION_SCREEN) {
            PermissionScreen(navController = navController)
        }

        composable(Routes.LOCATION_SCREEN) {
            LocationPermission(navController = navController)
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController = navController)
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
                countryCode2 = backStackEntry.arguments?.getString("countryCode2") ?: "",
                phoneNumber1 = backStackEntry.arguments?.getString("phoneNumber1") ?: "",
                phoneNumber2 = backStackEntry.arguments?.getString("phoneNumber2") ?: "",
                navController= navController
            )
        }
    }
}
