import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.LocationService
import com.Siddharth.chat.PreferencesHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val allGranted = permissionsState.permissions.all { it.status.isGranted }

    val preferencesHelper = PreferencesHelper(context)
    val setupDone = preferencesHelper.isAppSetupDone()

    // State to show confirmation dialog
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Allow Location",
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
                        listOf(
                        Color(0xFF3A7BD5), // Blue
                        Color(0xFF00D2FF)) // Light Cya
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
               // Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Enable location Service",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFFF176),
                    modifier=Modifier.padding(horizontal=18.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "To ensure we can notify your contacts in an emergency, we need access to your location every few seconds. Your location is only used for safety purposes.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Button to show confirmation dialog
                Row(modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC0CF69),
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Text("Enable Location Service")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier=Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            if (allGranted) {
                                preferencesHelper.setAppSetupDone(true)
                                navController.navigate(Routes.MAIN_SCREEN) {
                                    popUpTo(Routes.INTRO_SCREEN) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please grant all permissions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        enabled = allGranted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (allGranted) Color(0xFFC0CF69) else Color.Gray,
                            contentColor = Color.DarkGray
                        )
                    ) {
                        Text("Continue")
                    }
                }

                // Confirmation Dialog
                if (showDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = {
                            Text(
                                "Enable Location Service",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF3A7BD5)
                            )
                        },
                        text = {
                            Text(
                                "To keep you safe, SafeSteps needs access to your location. " +
                                        "This allows the app to send alerts to your emergency contacts during an SOS.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val intent = Intent(context, LocationService::class.java)
                                    context.startService(intent)
                                    showDialog = false
                                    Toast.makeText(context, "Location Service Enabled", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFC0CF69),
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Text("Enable")
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = { showDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD3D3D3),
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationPermissionPreview() {
    MaterialTheme {
        LocationPermission(navController = NavController(context = LocalContext.current))
    }
}