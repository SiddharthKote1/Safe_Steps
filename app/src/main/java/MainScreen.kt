package com.example.chat

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    user: FirebaseUser?,
    innerPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val preferencesHelper = PreferencesHelper(context)

    var countryCode1 by remember { mutableStateOf("+91") }
    var phoneNumber1 by remember { mutableStateOf("") }
    var countryCode2 by remember { mutableStateOf("+91") }
    var phoneNumber2 by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    LaunchedEffect(Unit) {
        val userData = preferencesHelper.getUserData()
        userData?.let {
            name = it.name
            age = it.age
            dateOfBirth = it.dob
            phoneNumber1 = it.phone1
            phoneNumber2 = it.phone2
            countryCode1 = it.countryCode1
            countryCode2 = it.countryCode2
        }
    }

    val datePicker = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dateOfBirth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year, month, day
    ).apply {
        datePicker.maxDate = calendar.timeInMillis
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Name", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true
        )

        Text("Age", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = age,
            onValueChange = {
                if (it.length <= 3 && it.isDigitsOnly()) {
                    age = it
                }
            },
            label = { Text("Enter your age") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(imageVector = Icons.Default.Face, contentDescription = "Age")
            }
        )

        Text("Date of Birth", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = {},
            label = { Text("Select your date of birth") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { datePicker.show() }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select DOB")
                }
            }
        )

        Text("Phone Numbers", style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = countryCode1,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() || char == '+' }) {
                        countryCode1 = it
                    }
                },
                label = { Text("Code") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneNumber1,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber1 = it
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                trailingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = "Phone number")
                }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = countryCode2,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() || char == '+' }) {
                        countryCode2 = it
                    }
                },
                label = { Text("Code") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneNumber2,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber2 = it
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                trailingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = "Phone number")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filled = name.isNotBlank() && age.isNotBlank() && dateOfBirth.isNotBlank() &&
                phoneNumber1.length == 10 && phoneNumber2.length == 10

        Button(
            onClick = {
                preferencesHelper.saveUserData(
                    name = name,
                    age = age,
                    dob = dateOfBirth,
                    phone1 = phoneNumber1,
                    phone2 = phoneNumber2,
                    countryCode1 = countryCode1,
                    countryCode2 = countryCode2
                )
                navController.navigate("NeeScreen/$name/$countryCode1/$countryCode2/$phoneNumber1/$phoneNumber2") {
                    popUpTo("MainScreen") { inclusive = true }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (filled) Color(0xFF0073E6) else Color.LightGray
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 8.dp),
            enabled = filled
        ) {
            Text("Continue")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    MainScreen(navController = navController, user = null)
}
