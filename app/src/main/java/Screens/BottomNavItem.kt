package Screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    // Home → NeeScreen (parameterized, handled specially)
    object Home : BottomNavItem(
        route = "NeeScreen",
        icon = Icons.Default.Home,
        label = "Home"
    )

    // Edit → MainScreen
    object Edit : BottomNavItem(
        route = Routes.MAIN_SCREEN,
        icon = Icons.Default.Edit,
        label = "Edit"
    )

    // Info → HelpScreen
    object Info : BottomNavItem(
        route = Routes.HELP_SCREEN,
        icon = Icons.Default.Info,
        label = "Info"
    )
}
