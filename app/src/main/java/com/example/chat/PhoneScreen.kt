package com.example.chat

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun PhoneScreen(navController: NavController) {
    var countryCode by remember { mutableStateOf("91") } // Default to India
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.chatpi),
            contentDescription = "App Logo"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Enter Phone Number",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = countryCode,
                onValueChange = {
                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                        countryCode = it
                    }
                },
                label = { Text("Code") },
                modifier = Modifier.width(80.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                        phoneNumber = it
                    }
                },
                label = { Text("Phone Number") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (phoneNumber.length < 10) {
                    Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                onLoginClicked(
                    context = context,
                    navController = navController,
                    countryCode = countryCode,
                    phoneNumber = phoneNumber,
                    onCodeSend = { isLoading = false }
                )
            },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0073E6)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Generate OTP")
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PhoneScreenPreview() {
    PhoneScreen(navController = rememberNavController())
}