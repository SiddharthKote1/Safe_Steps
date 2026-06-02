package com.Siddharth.SafeSteps

import com.Siddharth.SafeSteps.PreferencesHelper
import android.Manifest
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.LocationService
import com.Siddharth.SafeSteps.screens.Routes
import com.Siddharth.SafeSteps.screens.StepProgressIndicator
import com.Siddharth.SafeSteps.screens.getPremiumBackgroundBrush
import com.Siddharth.SafeSteps.screens.PremiumCardModifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun LocationPermission(navController: NavController) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val preferencesHelper = PreferencesHelper(context)

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val allGranted = permissionsState.permissions.all {
        it.status.isGranted
    }

    LaunchedEffect(allGranted) {
        if (allGranted) {

            preferencesHelper.setAppSetupDone(true)

            context.startService(
                Intent(context, LocationService::class.java)
            )

            Toast.makeText(
                context,
                "Location Enabled",
                Toast.LENGTH_SHORT
            ).show()
            
            navController.navigate(Routes.SETUP_CONTACTS) {
                popUpTo(Routes.LOCATION_SCREEN) { inclusive = true }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getPremiumBackgroundBrush())
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            
            StepProgressIndicator(currentStep = 2, totalSteps = 3)
            
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F4FF).copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Allow Location Access",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We use your location only during emergencies to notify your contacts and send help.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            FeatureCard(
                icon = Icons.Default.ShareLocation,
                iconColor = Color(0xFF22C55E),
                text = "Used only in emergencies"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FeatureCard(
                icon = Icons.Default.Lock,
                iconColor = Color(0xFF2563EB),
                text = "Never tracked in background"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FeatureCard(
                icon = Icons.Default.Person,
                iconColor = Color(0xFF8B5CF6),
                text = "You're always in control"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    permissionsState.launchMultiplePermissionRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Enable Location Access",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    text: String
) {
    Box(
        modifier = PremiumCardModifier().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                color = Color.DarkGray
            )
        }
    }
}