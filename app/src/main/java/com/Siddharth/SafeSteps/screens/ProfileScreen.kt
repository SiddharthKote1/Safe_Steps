package com.Siddharth.SafeSteps.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.PreferencesHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    
    val userData = preferencesHelper.getUserData()
    val ownPhone = preferencesHelper.getOwnPhone() ?: ""
    val ownCountryCode = preferencesHelper.getOwnCountryCode() ?: ""
    
    val userName = userData?.name ?: "User"
    val age = userData?.age ?: "N/A"
    val contact1 = "${userData?.countryCode1 ?: ""} ${userData?.phone1 ?: "Not Set"}"
    val contact2 = if (userData?.phone2.isNullOrBlank()) "Not Set" else "${userData?.countryCode2 ?: ""} ${userData?.phone2}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = Modifier.background(getPremiumBackgroundBrush())
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1).uppercase(),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "$ownCountryCode $ownPhone",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Personal Info Card
            ProfileSectionTitle("Personal Information")
            Box(
                modifier = PremiumCardModifier()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRowItem(icon = Icons.Default.PhoneAndroid, label = "Phone", value = "$ownCountryCode $ownPhone")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha=0.1f))
                    ProfileRowItem(icon = Icons.Default.Cake, label = "Age", value = "$age years")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha=0.1f))
                    ProfileRowItem(icon = Icons.Default.Bloodtype, label = "Blood Group", value = "O+ (Default)")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Emergency Contacts Card
            ProfileSectionTitle("Emergency Contacts")
            Box(
                modifier = PremiumCardModifier()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ProfileRowItem(icon = Icons.Default.Contacts, label = "Primary Contact", value = contact1)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline.copy(alpha=0.1f))
                    ProfileRowItem(icon = Icons.Default.Contacts, label = "Secondary Contact", value = contact2)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Logout Button
            Button(
                onClick = {
                    preferencesHelper.saveAccessToken("")
                    preferencesHelper.setAppSetupDone(false)
                    // You could clear user data here as well, but usually tokens determine auth state
                    
                    navController.navigate(Routes.LOGIN_SCREEN) {
                        popUpTo(0) { inclusive = true } // Clear entire backstack
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5B6A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.DarkGray,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun ProfileRowItem(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
        }
    }
}
