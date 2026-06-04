package com.Siddharth.SafeSteps.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.PreferencesHelper
import com.Siddharth.SafeSteps.contactdataclass.AddContactRequest
import com.Siddharth.SafeSteps.data.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun SetupContactsScreen(navController: NavController) {
    val context = LocalContext.current
    val preferencesHelper = remember { PreferencesHelper(context) }
    val coroutineScope = rememberCoroutineScope()
    
    var phone1 by remember { mutableStateOf("") }
    var selectedCountry1 by remember { mutableStateOf(defaultCountries[0]) }
    
    var phone2 by remember { mutableStateOf("") }
    var selectedCountry2 by remember { mutableStateOf(defaultCountries[0]) }
    
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getPremiumBackgroundBrush())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            StepProgressIndicator(currentStep = 3, totalSteps = 3)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Add at least one primary contact. We'll notify them in case of emergency.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Primary Contact
            Text(
                text = "Primary Contact (Required)",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PhoneInputFieldWithCountry(
                phoneNumber = phone1,
                onPhoneNumberChange = { if(it.length <= 15) phone1 = it },
                selectedCountry = selectedCountry1,
                onCountryChange = { selectedCountry1 = it }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Secondary Contact
            Text(
                text = "Secondary Contact (Optional)",
                style = MaterialTheme.typography.labelMedium,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PhoneInputFieldWithCountry(
                phoneNumber = phone2,
                onPhoneNumberChange = { if(it.length <= 15) phone2 = it },
                selectedCountry = selectedCountry2,
                onCountryChange = { selectedCountry2 = it }
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Complete Button
            Button(
                onClick = { 
                    if(phone1.isNotBlank()) {
                        completeSetup(
                            navController = navController,
                            preferencesHelper = preferencesHelper,
                            coroutineScope = coroutineScope,
                            setLoading = { isLoading = it },
                            phone1 = phone1,
                            countryCode1 = selectedCountry1.code,
                            phone2 = phone2,
                            countryCode2 = selectedCountry2.code
                        )
                    } else {
                        Toast.makeText(context, "Primary contact is required", Toast.LENGTH_SHORT).show()
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
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Complete Setup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

private fun completeSetup(
    navController: NavController,
    preferencesHelper: PreferencesHelper,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    setLoading: (Boolean) -> Unit,
    phone1: String,
    countryCode1: String,
    phone2: String,
    countryCode2: String
) {
    setLoading(true)
    val userData = preferencesHelper.getUserData()
    
    // Save final state
    preferencesHelper.saveUserData(
        name = userData?.name ?: "User",
        age = userData?.age ?: "20",
        phone1 = phone1,
        phone2 = phone2,
        countryCode1 = countryCode1,
        countryCode2 = countryCode2
    )
    
    coroutineScope.launch {
        try {
            // Upload primary contact
            RetrofitClient.apiService.addContact(
                AddContactRequest(
                    name = "Primary Contact",
                    phoneNumber = countryCode1 + phone1,
                    priority = "Primary",
                    relationship = "Family"
                )
            )
            
            // Upload secondary contact if provided
            if (phone2.isNotBlank()) {
                RetrofitClient.apiService.addContact(
                    AddContactRequest(
                        name = "Secondary Contact",
                        phoneNumber = countryCode2 + phone2,
                        priority = "Secondary",
                        relationship = "Family"
                    )
                )
            }
        } catch (e: Exception) {
            // Log but don't fail, user can retry in profile
        } finally {
            setLoading(false)
            preferencesHelper.setAppSetupDone(true) // Mark completely done
            
            val finalData = preferencesHelper.getUserData()
            navController.navigate(
                neeScreenRoute(
                    name = finalData?.name,
                    countryCode1 = finalData?.countryCode1,
                    countryCode2 = finalData?.countryCode2,
                    phoneNumber1 = finalData?.phone1,
                    phoneNumber2 = finalData?.phone2
                )
            ) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
