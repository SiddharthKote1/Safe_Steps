package Screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.Siddharth.SafeSteps.EmergencyHelper
import com.Siddharth.chat.PreferencesHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NeeScreen(
    name: String = "",
    countryCode1: String = "+91",
    countryCode2: String = "+91",
    phoneNumber1: String = "",
    phoneNumber2: String = "",
    navController: NavController
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)

    var userName by remember { mutableStateOf(name) }
    var userCountryCode1 by remember { mutableStateOf(countryCode1) }
    var userCountryCode2 by remember { mutableStateOf(countryCode2) }
    var userPhone1 by remember { mutableStateOf(phoneNumber1) }
    var userPhone2 by remember { mutableStateOf(phoneNumber2) }

    // Load saved data
    LaunchedEffect(Unit) {
        val userData = preferencesHelper.getUserData()
        userData?.let {
            userName = it.name
            userCountryCode1 = it.countryCode1
            userCountryCode2 = it.countryCode2
            userPhone1 = it.phone1
            userPhone2 = it.phone2
        }
    }

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

    // Request once
    var requestedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted && !requestedOnce) {
            requestedOnce = true
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            EmergencyHelper.contact1 = "$userCountryCode1$userPhone1"
            EmergencyHelper.contact2 = "$userCountryCode2$userPhone2"
        }
    }

    // 🔽 Menu state
    var menuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF3A7BD5),
                        Color(0xFF00D2FF)
                    )
                )
            )
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sos))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Emergency Info",
                    style = MaterialTheme.typography.headlineMedium.copy(color = Color.White)
                )

                // Menu Icon
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Profile") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Routes.MAIN_SCREEN)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                menuExpanded = false
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Help") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate(Routes.HELP_SCREEN)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.45f)
                    .aspectRatio(1f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        InfoRow("Name", userName)
                        InfoRow("Phone 1", "$userCountryCode1 $userPhone1")
                        InfoRow("Phone 2", "$userCountryCode2 $userPhone2")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color(0xFF1976D2),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = value.ifEmpty { "Not set" },
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NeeScreenPreview() {
    NeeScreen(
        name = "Siddharth Kote",
        countryCode1 = "+91",
        countryCode2 = "+91",
        phoneNumber1 = "9876543210",
        phoneNumber2 = "9123456780",
        navController = NavController(context = LocalContext.current)
    )
}
