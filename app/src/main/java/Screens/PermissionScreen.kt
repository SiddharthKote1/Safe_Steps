package Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    navController: NavController
) {

    val context = LocalContext.current

    val permissions = buildList {
        add(Manifest.permission.SEND_SMS)
        add(Manifest.permission.CALL_PHONE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    var showAccessibilityDialog by remember {
        mutableStateOf(false)
    }

    var accessibilityEnabled by remember {
        mutableStateOf(
            isAccessibilityServiceEnabled(context)
        )
    }

    LaunchedEffect(Unit) {
        accessibilityEnabled =
            isAccessibilityServiceEnabled(context)
    }

    val canContinue =
        permissionsState.allPermissionsGranted &&
                accessibilityEnabled

    Scaffold(
        containerColor = Color.White
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Let's set up\nyour safety",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We need a few permissions to keep you safe during emergencies.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            PermissionCard(
                iconColor = Color(0xFF7C4DFF),
                icon = {
                    Icon(
                        Icons.Default.Accessibility,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                title = "Accessibility Service",
                subtitle = if (accessibilityEnabled)
                    "Enabled"
                else
                    "Required to detect SOS triggers"
            ) {
                showAccessibilityDialog = true
            }

            PermissionCard(
                iconColor = Color(0xFF2ECC71),
                icon = {
                    Icon(
                        Icons.Default.Message,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                title = "SMS Permission",
                subtitle = "To send SOS alerts and share location"
            ) {
                permissionsState.launchMultiplePermissionRequest()
            }

            PermissionCard(
                iconColor = Color(0xFF3B82F6),
                icon = {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                title = "Phone Permission",
                subtitle = "Call emergency contacts"
            ) {
                permissionsState.launchMultiplePermissionRequest()
            }

            PermissionCard(
                iconColor = Color(0xFFF4B400),
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                title = "Notification Permission",
                subtitle = "Receive emergency alerts"
            ) {
                permissionsState.launchMultiplePermissionRequest()
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {

                    accessibilityEnabled =
                        isAccessibilityServiceEnabled(context)

                    if (!permissionsState.allPermissionsGranted) {

                        permissionsState.launchMultiplePermissionRequest()

                        Toast.makeText(
                            context,
                            "Please grant all permissions",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    if (!accessibilityEnabled) {

                        Toast.makeText(
                            context,
                            "Please enable Accessibility Service",
                            Toast.LENGTH_SHORT
                        ).show()

                        context.startActivity(
                            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        )

                        return@Button
                    }
                    navController.navigate(Routes.LOCATION_SCREEN)


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {
                Text("Continue")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showAccessibilityDialog) {

            AlertDialog(
                onDismissRequest = {
                    showAccessibilityDialog = false
                },
                title = {
                    Text("Accessibility Service")
                },
                text = {
                    Text(
                        "Enable Accessibility Service so SafeSteps can detect SOS triggers and provide emergency assistance."
                    )
                },
                confirmButton = {

                    TextButton(
                        onClick = {

                            context.startActivity(
                                Intent(
                                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                                )
                            )

                            showAccessibilityDialog = false
                        }
                    ) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {

                    TextButton(
                        onClick = {
                            showAccessibilityDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

fun isAccessibilityServiceEnabled(
    context: Context
): Boolean {

    return try {

        Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        ) == 1

    } catch (e: Exception) {
        false
    }
}

@Composable
fun PermissionCard(
    iconColor: Color,
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F7F7)
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = iconColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}