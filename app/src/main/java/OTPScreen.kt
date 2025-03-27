package com.example.chat

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun OTPScreen(navController: NavController) {
    var otp by remember { mutableStateOf("") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var isLoading by remember { mutableStateOf(false) }
    var canResend by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30) }
    val context = LocalContext.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

    LaunchedEffect(key1 = canResend) {
        if (!canResend) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            canResend = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Enter OTP",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(6) { index ->
                OutlinedTextField(
                    value = otp.getOrNull(index)?.toString() ?: "",
                    onValueChange = { newValue ->
                        if (newValue.length == 1 && newValue.all { it.isDigit() }) {
                            otp = otp.take(index) + newValue + otp.drop(index + 1)
                            if (index < 5) focusRequesters[index + 1].requestFocus()
                            else {
                                verifyPhoneNumberWithCode(context, storedVerificationId, otp, navController)
                            }
                        } else if (newValue.isEmpty()) {
                            otp = otp.take(index) + otp.drop(index + 1)
                            if (index > 0) focusRequesters[index - 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .width(48.dp)
                        .focusRequester(focusRequesters[index]),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (otp.length == 6) {
                    verifyPhoneNumberWithCode(context, storedVerificationId, otp, navController)
                } else {
                    Toast.makeText(context, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = otp.length == 6 && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0073E6)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
                Text("Verify")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                if (canResend) {
                    resendOTP(context, navController, storedCountryCode, storedPhoneNumber)
                    timeLeft = 60
                    canResend = false
                }
            },
            enabled = canResend
        ) {
            Text(
                text = if (canResend) "Resend OTP" else "Resend OTP in $timeLeft sec",
                color = if (canResend) Color(0xFF0073E6) else Color.Gray
            )
        }
    }


