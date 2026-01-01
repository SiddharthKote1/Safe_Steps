package Screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import PreferencesHelper
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
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

    val datePicker = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                dateOfBirth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = calendar.timeInMillis
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF3A7BD5),
                        Color(0xFF00D2FF))
                )
            )
            .padding(innerPadding)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile Setup",
                style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name",
                            color=Color.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledLabelColor = Color.Gray,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.DarkGray,
                            disabledPlaceholderColor = Color.Gray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray
                        )
                    )

                    OutlinedTextField(
                        value = age,
                        onValueChange = {
                            if (it.length <= 2 && it.isDigitsOnly()) age = it
                        },
                        label = { Text("Age",
                            color=Color.Black) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledLabelColor = Color.Gray,
                            disabledContainerColor = Color.Transparent,
                            disabledIndicatorColor = Color.DarkGray,
                            disabledPlaceholderColor = Color.Gray,
                            disabledLeadingIconColor = Color.Gray,
                            disabledTrailingIconColor = Color.Gray
                        )

                        )


                    Box(modifier=Modifier.fillMaxWidth()
                        .clickable{datePicker.show()}) {
                        OutlinedTextField(
                            value = dateOfBirth,
                            onValueChange = {},
                            label = { Text("Date of Birth",
                                color=Color.Black)},
                            readOnly = true,
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { datePicker.show() },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                        ))
                    }

                    Text("Phone Numbers", style = MaterialTheme.typography.bodyMedium)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = countryCode1,
                            onValueChange = {
                                if (it.length <= 4 && it.all { c -> c.isDigit() || c == '+' }) {
                                    countryCode1 = it
                                }
                            },
                            label = { Text("Code",
                                color=Color.Black) },
                            modifier = Modifier.width(90.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
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
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = countryCode2,
                            onValueChange = {
                                if (it.length <= 4 && it.all { c -> c.isDigit() || c == '+' }) {
                                    countryCode2 = it
                                }
                            },
                            label = { Text("Code",
                                color=Color.Black) },
                            modifier = Modifier.width(90.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                        OutlinedTextField(
                            value = phoneNumber2,
                            onValueChange = {
                                if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                    phoneNumber2 = it
                                }
                            },
                            label = { Text("Phone Number",
                                color=Color.Black) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.DarkGray,
                                disabledPlaceholderColor = Color.Gray,
                                disabledLeadingIconColor = Color.Gray,
                                disabledTrailingIconColor = Color.Gray
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val filled = name.isNotBlank() && age.isNotBlank() && dateOfBirth.isNotBlank() &&
                            phoneNumber1.length == 10 && phoneNumber2.length == 10

                    Row(horizontalArrangement = Arrangement.Center,
                        modifier=Modifier.fillMaxWidth()) {
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
                            enabled = filled,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .padding(horizontal = 32.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = if (filled) Color(0xFFC0CF69) else Color.LightGray
                            )
                        ) {
                            Text(
                                "Continue",
                                color = if (filled) Color.DarkGray else Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            navController = rememberNavController()
        )
    }
}
