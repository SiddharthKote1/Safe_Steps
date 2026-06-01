package Screens

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

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FC))
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box {

                IconButton(
                    onClick = {
                        menuExpanded = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("Accessibility Settings")
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            menuExpanded = false

                            context.startActivity(
                                Intent(
                                    Settings.ACTION_ACCESSIBILITY_SETTINGS
                                )
                            )
                        }
                    )
                }
            }

            Text(
                text = "SafeSteps",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { }
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))


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
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(R.drawable.img),
                contentDescription = null)

        }

        Text(
            text = "Hold volume button for 5 seconds for sos",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            ),
            color = Color.DarkGray,
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
    }
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
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.SemiBold
        )
    }
}
