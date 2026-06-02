package Screens

import com.Siddharth.SafeSteps.PreferencesHelper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import java.util.*
import kotlinx.coroutines.launch
import data.RetrofitClient
import Screens.SessionManager
import AuthDataClass.RegisterRequest
import ContactDataClass.AddContactRequest
import android.util.Log

@Composable
fun MainScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var ownPhoneNumber by remember { mutableStateOf("") }
    var ownCountryCode by remember { mutableStateOf("+91") }

    var phoneNumber1 by remember { mutableStateOf("") }
    var phoneNumber2 by remember { mutableStateOf("") }

    var countryCode1 by remember { mutableStateOf("+91") }
    var countryCode2 by remember { mutableStateOf("+91") }

    var isLoading by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()

    LaunchedEffect(Unit) {
        val userData = preferencesHelper.getUserData()
        userData?.let {
            name = it.name
            age = it.age
            phoneNumber1 = it.phone1
            phoneNumber2 = it.phone2
            countryCode1 = it.countryCode1
            countryCode2 = it.countryCode2
        }
        preferencesHelper.getOwnPhone()?.let { ownPhoneNumber = it }
        preferencesHelper.getOwnCountryCode()?.let { ownCountryCode = it }
    }

    val isFormValid =
        name.isNotBlank() &&
                age.isNotBlank() &&
                ownPhoneNumber.length == 10 &&
                phoneNumber1.length == 10 &&
                phoneNumber2.length == 10 &&
                !isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))


        Text(
            text = "Profile Setup",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Tell us about yourself",
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = Color(0xFFF2F5FF),
                    shape = CircleShape
                )
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFF2563EB),
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Full Name",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter your name")
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Age",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.length <= 2 && it.isDigitsOnly()) {
                    age = it
                }
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Enter your age")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Your Phone Number",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = ownCountryCode,
                onValueChange = {
                    if (it.length <= 5) {
                        ownCountryCode = it
                    }
                },
                modifier = Modifier.width(90.dp),
                placeholder = {
                    Text("+91")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = ownPhoneNumber,
                onValueChange = {
                    if (it.length <= 10 && it.isDigitsOnly()) {
                        ownPhoneNumber = it
                    }
                },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF2563EB)
                    )
                },
                placeholder = {
                    Text("Phone Number")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Emergency Contacts",
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = countryCode1,
                onValueChange = {
                    if (it.length <= 5) {
                        countryCode1 = it
                    }
                },
                modifier = Modifier.width(90.dp),
                placeholder = {
                    Text("+91")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber1,
                onValueChange = {
                    if (it.length <= 10 && it.isDigitsOnly()) {
                        phoneNumber1 = it
                    }
                },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF2563EB)
                    )
                },
                placeholder = {
                    Text("Phone Number")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = countryCode2,
                onValueChange = {
                    if (it.length <= 5) {
                        countryCode2 = it
                    }
                },
                modifier = Modifier.width(90.dp),
                placeholder = {
                    Text("+91")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber2,
                onValueChange = {
                    if (it.length <= 10 && it.isDigitsOnly()) {
                        phoneNumber2 = it
                    }
                },
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = null,
                        tint = Color(0xFF2563EB)
                    )
                },
                placeholder = {
                    Text("Phone Number")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    try {
                        val fullPhone = ownCountryCode + ownPhoneNumber
                        // Call Register API
                        val registerResponse = RetrofitClient.apiService.register(
                            RegisterRequest(
                                age = age,
                                blood_group = "O+", // Default for now
                                date_of_birth = "2000-01-01",
                                full_name = name,
                                gender = "Unknown",
                                medical_notes = "",
                                phone = fullPhone,
                                preferred_language = "English"
                            )
                        )
                        
                        // Save token
                        val token = registerResponse.token
                        SessionManager.token = token
                        preferencesHelper.saveOwnPhone(ownPhoneNumber, ownCountryCode)
                        preferencesHelper.saveUserData(
                            name = name,
                            age = age,
                            phone1 = phoneNumber1,
                            phone2 = phoneNumber2,
                            countryCode1 = countryCode1,
                            countryCode2 = countryCode2
                        )
                        
                        // Add Emergency Contacts
                        try {
                            RetrofitClient.apiService.addContact(
                                AddContactRequest(
                                    name = "Emergency Contact 1",
                                    phoneNumber = countryCode1 + phoneNumber1,
                                    priority = "Primary",
                                    relationship = "Family"
                                )
                            )
                            RetrofitClient.apiService.addContact(
                                AddContactRequest(
                                    name = "Emergency Contact 2",
                                    phoneNumber = countryCode2 + phoneNumber2,
                                    priority = "Secondary",
                                    relationship = "Family"
                                )
                            )
                        } catch (e: Exception) {
                            Log.e("MainScreen", "Failed to add contacts", e)
                        }
                        
                        Toast.makeText(context, "Profile Saved Successfully", Toast.LENGTH_SHORT).show()
                        navController.navigate("NeeScreen/$name/$countryCode1/$countryCode2/$phoneNumber1/$phoneNumber2") {
                            popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Log.e("MainScreen", "Registration failed", e)
                        // If phone already registered, try login
                        try {
                             val loginResponse = RetrofitClient.apiService.login(AuthDataClass.LoginRequest(ownCountryCode + ownPhoneNumber))
                             SessionManager.token = loginResponse.token
                             preferencesHelper.saveOwnPhone(ownPhoneNumber, ownCountryCode)
                             navController.navigate("NeeScreen/$name/$countryCode1/$countryCode2/$phoneNumber1/$phoneNumber2") {
                                popUpTo(Routes.MAIN_SCREEN) { inclusive = true }
                             }
                        } catch (loginError: Exception) {
                             Toast.makeText(context, "Registration/Login Failed: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB),
                disabledContainerColor = Color.LightGray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Continue")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}