package com.Siddharth.SafeSteps.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import com.Siddharth.SafeSteps.PreferencesHelper
import com.Siddharth.SafeSteps.R
import com.Siddharth.SafeSteps.viewmodel.AuthState
import com.Siddharth.SafeSteps.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    var phoneOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(defaultCountries[0]) }

    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val preferencesHelper = remember { PreferencesHelper(context) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                val user = preferencesHelper.getUserData()
                val isUserDataComplete = user != null && user.name.isNotBlank() && user.phone1.isNotBlank()
                
                val nextRoute = when {
                    !preferencesHelper.isAppSetupDone() -> Routes.PERMISSION_SCREEN
                    !isUserDataComplete -> Routes.SETUP_CONTACTS
                    else -> "NeeScreen/${Uri.encode(user!!.name)}/${Uri.encode(user.countryCode1)}/${Uri.encode(user.countryCode2)}/${Uri.encode(user.phone1)}/${Uri.encode(user.phone2)}"
                }
                
                navController.navigate(nextRoute) {
                    popUpTo(Routes.LOGIN_SCREEN) { inclusive = true }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    val isEmail = phoneOrEmail.contains("@") || phoneOrEmail.any { it.isLetter() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "SafeSteps Logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Welcome back.",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Sign in to access your safety network.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Input Field Header
            Text(
                text = if (isEmail) "Email Address" else "Phone Number or Email",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            if (isEmail) {
                CustomInputField(
                    value = phoneOrEmail,
                    onValueChange = { phoneOrEmail = it },
                    icon = Icons.Default.Email,
                    placeholder = "Enter email address",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                )
            } else {
                PhoneInputFieldWithCountry(
                    phoneNumber = phoneOrEmail,
                    onPhoneNumberChange = { phoneOrEmail = it },
                    selectedCountry = selectedCountry,
                    onCountryChange = { selectedCountry = it }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Password Field
            Text(
                text = "Password",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            CustomPasswordField(
                value = password,
                onValueChange = { password = it }
            )
            
            val isPhoneValid = isEmail || phoneOrEmail.length == 10
            if (!isEmail && phoneOrEmail.isNotEmpty() && !isPhoneValid) {
                Text(
                    text = "Invalid number: Must be exactly 10 digits",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Login Button
            val isButtonEnabled = phoneOrEmail.isNotBlank() && password.isNotBlank() && isPhoneValid && authState !is AuthState.Loading
            Button(
                onClick = { 
                    if (isButtonEnabled) {
                        val loginUsername = if (isEmail) phoneOrEmail else (selectedCountry.code + phoneOrEmail)
                        authViewModel.login(loginUsername, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                enabled = isButtonEnabled
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Sign In",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Register Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Create one",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .clickable { navController.navigate(Routes.REGISTER_SCREEN) }
                        .padding(4.dp)
                )
            }
        }
    }
}
