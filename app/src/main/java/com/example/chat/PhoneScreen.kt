package com.example.chat

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun PhoneScreen(navController: NavController) {
    var countryCode by remember { mutableStateOf("91") }
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity

    // Only initialize if activity is not null
    val googleSignInHelper = remember(activity) {
        activity?.let { GoogleSignInHelper(it) }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
        try {
            val account = task.result
            Toast.makeText(context, "Signed in as ${account?.email}", Toast.LENGTH_SHORT).show()

            navController.navigate(Routes.MAIN_SCREEN) {
                popUpTo(Routes.PHONE_SCREEN) { inclusive = true }
            }

        } catch (e: ApiException) {
            Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFC1CC), // light pink
                        Color(0xFFB5F6CC), // mint
                        Color(0xFFB2EBF2)
                    )
                )
            )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(id = R.drawable.safe),
            contentDescription = "App Logo",
            modifier = Modifier.size(300.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Enter Phone Number",
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "You will receive a 6 digit code for phone number verification",
            modifier = Modifier
                .width(280.dp)
                .padding(start = 30.dp),
            color = Color.Gray
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
                    Toast.makeText(
                        context,
                        "Please enter a valid 10-digit phone number",
                        Toast.LENGTH_SHORT
                    ).show()
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
                containerColor = Color(0xFF26A69A)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Generate OTP")
        }
        Spacer(modifier = Modifier.height(18.dp))

        Text("OR", color = Color.Black)
        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                googleSignInHelper?.let {
                    val signInIntent = it.client.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.LightGray), // subtle border
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.goo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign in with Google",
                color = Color.Black,
                fontSize = 16.sp
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PhoneScreenPreview() {
    PhoneScreen(navController = rememberNavController())
}
