package com.example.chat

import Room.RoomViewModel
import Room.User
import Room.UserDao
import Room.UserRepository
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    user: FirebaseUser?,
    viewModel: RoomViewModel
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var countryCode1 by remember { mutableStateOf("+91") }
    var phoneNumber1 by remember { mutableStateOf("") }
    var countryCode2 by remember { mutableStateOf("+91") }
    var phoneNumber2 by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    // Date picker setup
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            dateOfBirth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        },
        year,
        month,
        day
    ).apply {
        datePicker.maxDate = calendar.timeInMillis
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                actions = {
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.Black
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.width(180.dp)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                onClick = {
                                    expanded = false
                                    // navController.navigate("profile")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                onClick = { expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Notifications") },
                                onClick = { expanded = false }
                            )
                            if (user != null) {
                                DropdownMenuItem(
                                    text = { Text("Log Out") },
                                    onClick = {
                                        expanded = false
                                        // FirebaseAuth.getInstance().signOut()
                                    }
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Name", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Age", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = age,
                onValueChange = {
                    if (it.length <= 3 && it.all { char -> char.isDigit() }) {
                        age = it
                    }
                },
                label = { Text("Enter your age") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Phone number"
                    )
                }
            )

            Text("Date of Birth", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = {},
                label = { Text("Select your date of birth") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select the dob of the person2") // Replace with a calendar icon if desired
                    }
                }
            )

            Text("Phone Number", style = MaterialTheme.typography.labelLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone number"
                        )
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone number"
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            val filled= name.isNotBlank() && age.isNotBlank() && dateOfBirth.isNotBlank() &&
                    phoneNumber1.length ==10 && phoneNumber2.length ==10
            Button(
                onClick = {  if (filled) {
                    viewModel.insertUser(
                        User(
                            name = name,
                            age = age,
                            dateOfBirth = dateOfBirth,
                            phone1 = "$countryCode1$phoneNumber1",
                            phone2 = "$countryCode2$phoneNumber2"
                        ),
                    )
                }
                          },
                shape= RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (filled) Color(0xFF0073E6) else Color.LightGray
                ),
                modifier=Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Continue")
            }
        }
    }
}
/*
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            navController = rememberNavController(),
            user = null,
            viewModel = RoomViewModel(repository = UserRepository(userDao = UserDao))
        )
    }
}
*/
