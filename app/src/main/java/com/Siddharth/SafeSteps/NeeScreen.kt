package com.Siddharth.SafeSteps

import com.Siddharth.SafeSteps.PreferencesHelper
import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.animation.core.*
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.Siddharth.SafeSteps.EmergencyHelper
import com.Siddharth.SafeSteps.R
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.collectAsState
import com.Siddharth.SafeSteps.ThreatLevelManager
import com.Siddharth.SafeSteps.data.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class
)
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

    LaunchedEffect(Unit) {
        preferencesHelper.getUserData()?.let {
            userName = it.name
            userCountryCode1 = it.countryCode1
            userCountryCode2 = it.countryCode2
            userPhone1 = it.phone1
            userPhone2 = it.phone2
        }
    }

    val activeSessionId by ThreatLevelManager.activeSessionId.collectAsState()
    val threatLevel by ThreatLevelManager.threatLevel.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val permissionsState = rememberMultiplePermissionsState(
        permissions = buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.SEND_SMS)
            add(Manifest.permission.CALL_PHONE)
            add(Manifest.permission.RECORD_AUDIO)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (permissionsState.allPermissionsGranted) {
            EmergencyHelper.contact1 =
                "$userCountryCode1$userPhone1"

            EmergencyHelper.contact2 =
                "$userCountryCode2$userPhone2"
        }
    }

    var isStoppingSos by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val dashboardBackgroundColor = if (activeSessionId != null && threatLevel == "CRITICAL") {
        Color(0xFF7F1D1D)
    } else {
        Color(0xFFF8F9FC)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if(userName.isNotBlank()) userName.take(1).uppercase() else "U",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = userName.ifBlank { "User" },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("ProfileScreen")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Incident History") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("HistoryScreen")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Analytics Dashboard") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("AnalyticsScreen")
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Accessibility Settings") },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(dashboardBackgroundColor)
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Navigation Drawer"
                    )
                }

            Text(
                text = "SafeSteps",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Keeping this empty for symmetrical spacing since profile moved to drawer
            Spacer(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (activeSessionId != null) {
            val threatColor = when(threatLevel) {
                "LOW" -> com.Siddharth.SafeSteps.ui.theme.ThreatLow
                "MEDIUM" -> com.Siddharth.SafeSteps.ui.theme.ThreatMedium
                "HIGH" -> com.Siddharth.SafeSteps.ui.theme.ThreatHigh
                "CRITICAL" -> Color(0xFF991B1B)
                else -> Color.Gray
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = threatColor)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SOS ACTIVE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    val displayThreat = threatLevel ?: "UNKNOWN"
                    Text(
                        text = "Threat Level: $displayThreat",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("ChatScreen/${activeSessionId}")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.2f))
                        ) {
                            Text("AI Safety Chat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Hello, $userName",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "You are protected",
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFE9FFF1),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF22C55E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            PulsatingSOSButton(
                isSosActive = activeSessionId != null,
                isStopping = isStoppingSos,
                onClick = {
                    if (activeSessionId == null) {
                        EmergencyHelper.contact1 = "$userCountryCode1$userPhone1"
                        EmergencyHelper.contact2 = "$userCountryCode2$userPhone2"
                        EmergencyHelper.sendSmsAndCall(context)
                    } else {
                        // End SOS logic moved here
                        isStoppingSos = true
                        val currentSessionId = activeSessionId
                        coroutineScope.launch {
                            kotlinx.coroutines.delay(500) // Small delay to show Red color
                            try {
                                RetrofitClient.apiService.endSession()
                                context.stopService(Intent(context, com.Siddharth.SafeSteps.AudioStreamingService::class.java))
                                context.stopService(Intent(context, com.Siddharth.SafeSteps.LocationService::class.java))
                                ThreatLevelManager.clearSession()
                                isStoppingSos = false
                                if (currentSessionId != null) {
                                    navController.navigate("ReportScreen/${currentSessionId}")
                                }
                            } catch (e: Exception) {
                                isStoppingSos = false
                            }
                        }
                    }
                }
            )
        }

        Text(
            text = if (activeSessionId != null) "Help is on the way!" else "Tap to trigger SOS immediately",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            ),
            color = if (activeSessionId != null) MaterialTheme.colorScheme.error else Color.DarkGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))


        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(
                modifier = Modifier.padding(18.dp)
            ) {

                Text(
                    text = "Emergency Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(
                    label = "Contact 1",
                    value = "$userCountryCode1 $userPhone1"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    label = "Contact 2",
                    value = "$userCountryCode2 $userPhone2"
                )

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    label = "Accessibility",
                    value = "Enabled"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    label = "Location Tracking",
                    value = "Active"
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    label = "Emergency SMS",
                    value = "Ready"
                )
            }
        }
    } // End Column
    } // End ModalNavigationDrawer
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = Color.DarkGray
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PulsatingSOSButton(
    isSosActive: Boolean,
    isStopping: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSosActive && !isStopping) 1.05f else 1.15f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ), label = "pulse_scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(1000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ), label = "pulse_alpha"
    )

    // Colors: Idle = Primary (Violet/Blue), Active = Green, Stopping = Red
    val buttonColor = if (isStopping) {
        MaterialTheme.colorScheme.error 
    } else if (isSosActive) {
        Color(0xFF22C55E) // Green
    } else {
        MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isSosActive || isStopping) {
            Box(
                modifier = Modifier
                    .size((240f * scale).dp)
                    .background(
                        color = buttonColor.copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
            
            Box(
                modifier = Modifier
                    .size((200f * scale).dp)
                    .background(
                        color = buttonColor.copy(alpha = alpha * 1.5f),
                        shape = CircleShape
                    )
            )
        } else {
            val activeAlpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.6f,
                animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                    animation = androidx.compose.animation.core.tween(500),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                ), label = "active_alpha"
            )
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(
                        color = buttonColor.copy(alpha = activeAlpha),
                        shape = CircleShape
                    )
            )
        }

        Button(
            onClick = onClick,
            modifier = Modifier.size(160.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,
                pressedElevation = 2.dp
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = if(isSosActive) "STOP" else "SOS",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}
