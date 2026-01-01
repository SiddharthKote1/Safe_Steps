package Screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import PreferencesHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preferencesHelper = PreferencesHelper(context)
    val setupDone = preferencesHelper.isAppSetupDone()

    LaunchedEffect(setupDone) {
        if (setupDone) {
            navController.navigate(Routes.MAIN_SCREEN) {
                popUpTo(Routes.INTRO_SCREEN) { inclusive = true }
            }
        }
    }

    var showDialog by remember { mutableStateOf(false) }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.SEND_SMS)
            add(Manifest.permission.CALL_PHONE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    )

    var runtimePermissionsGranted by remember {
        mutableStateOf(permissionsState.permissions.all { it.status.isGranted })
    }

    var accessibilityEnabled by remember { mutableStateOf(isAccessibilityEnabled(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                accessibilityEnabled = isAccessibilityEnabled(context)
                runtimePermissionsGranted =
                    permissionsState.permissions.all { it.status.isGranted }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val allGranted by remember {
        derivedStateOf { runtimePermissionsGranted && accessibilityEnabled }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Access Permission",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF3A7BD5),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 20.dp, start = 32.dp, end = 20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "1. Enable Accessibility Service",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFFF176)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Accessibility service is required to detect SOS triggers quickly and automatically share your location with emergency contacts.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .defaultMinSize(minHeight = ButtonDefaults.MinHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC0CF69),
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Text("Enable Accessibility Service")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "2. Allow App Permissions",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFFF176)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SafeSteps app needs Location, SMS, Phone Call, and Notification permissions to detect SOS triggers, share your location, and alert your emergency contacts.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (!runtimePermissionsGranted) {
                                permissionsState.launchMultiplePermissionRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    "All permissions granted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .defaultMinSize(minHeight = ButtonDefaults.MinHeight),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC0CF69),
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Text("Grant Permissions")
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            if (allGranted) {
                                preferencesHelper.setAppSetupDone(true)
                                navController.navigate(Routes.LOCATION_SCREEN)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enable Accessibility service and grant permissions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC0CF69),
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Text("Continue")
                    }
                }
            }

            if (showDialog) {
                var isChecked by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = {},
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                                showDialog = false
                            },
                            enabled = isChecked
                        ) {
                            Text("Open Settings")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    },
                    title = { Text("Emergency SOS Access",
                        style= MaterialTheme.typography.headlineSmall) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                            Text(
                                "To keep you safe, this feature uses Accessibility Service to detect when you press both volume buttons together. " +
                                        "That action will instantly trigger an SOS alert — sharing your live location with your emergency contacts and sending help messages automatically."
                            )

                            Text(
                                "To turn this on:\n" +
                                        "1. Tap 'Open Settings' below.\n" +
                                        "2. Find \"SafeSteps\" in the list.\n" +
                                        "3. Turn the switch ON.\n" +
                                        "4. Confirm any prompts.\n" +
                                        "5. Come back here and tap Continue."
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it }
                                )
                                Text(
                                    "I agree to enable SOS detection via Accessibility Service."
                                )
                            }
                        }
                    },
                    containerColor = Color.White
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.CUPCAKE)
fun isAccessibilityEnabled(context: Context): Boolean {
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return !enabledServices.isNullOrEmpty() && enabledServices.contains(context.packageName)
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PermissionScreenPreview() {
    val navController = rememberNavController()
    PermissionScreen(navController = navController)
}
*/

