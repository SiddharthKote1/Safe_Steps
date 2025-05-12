package com.example.chat

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.*
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NeeScreen(
    navController: NavController,
    user: FirebaseUser?,
    name: String,
    countryCode1: String,
    countryCode2: String,
    phoneNumber1: String,
    phoneNumber2: String,
) {
    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    )

    // This ensures the permission request is only triggered once
    var requestedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        if (!permissionsState.allPermissionsGranted && !requestedOnce) {
            requestedOnce = true
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Emergency Alert") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Name: $name")
            Text("Phone No 1: $countryCode1 $phoneNumber1")
            Text("Phone No 2: $countryCode2 $phoneNumber2")
            Spacer(Modifier.height(20.dp))

            if (permissionsState.allPermissionsGranted) {
                Text("All required permissions granted.")
                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(context, LocationService::class.java)
                    context.startService(intent)
                }) {
                    Text("Start Location Service")
                }

                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(context, LocationService::class.java)
                    context.stopService(intent)
                }) {
                    Text("Stop Location Service")
                }
            } else {
                Text("Permissions are required to proceed.")
                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            }
        }
    }
}

