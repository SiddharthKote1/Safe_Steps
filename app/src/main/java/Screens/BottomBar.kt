package Screens

import PreferencesHelper
import android.net.Uri
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Edit,
        BottomNavItem.Info
    )

    NavigationBar {

        val currentRoute =
            navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = {

                    when (item) {

                        // HOME → NeeScreen with params
                        BottomNavItem.Home -> {
                            val user = preferencesHelper.getUserData() ?: return@NavigationBarItem

                            navController.navigate(
                                "NeeScreen/${Uri.encode(user.name)}/${Uri.encode(user.countryCode1)}/${Uri.encode(user.countryCode2)}/${Uri.encode(user.phone1)}/${Uri.encode(user.phone2)}"
                            ) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }

                        // EDIT & INFO → simple routes
                        else -> {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
            )
        }
    }
}
