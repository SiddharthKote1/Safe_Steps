package com.example.chat

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay

@Composable
fun OTPScreen(navController: NavController) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))

    val otpDigits = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var otp by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(canResend) {
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        LottieAnimation(
            composition,
            modifier = Modifier.size(300.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Enter OTP", fontSize = 24.sp)
        Text(
            text = "Sent to +$storedCountryCode$storedPhoneNumber",
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        OtpInput(
            otpDigits = otpDigits,
            focusRequesters = focusRequesters,
            onOtpChanged = { updatedOtp ->
                otp = updatedOtp
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (otp.length == 6) {
                    isLoading = true
                    verifyPhoneNumberWithCode(
                        context = context,
                        verificationId = storedVerificationId,
                        code = otp,
                        navController = navController,
                        onComplete = { success ->
                            isLoading = !success
                        }
                    )
                } else {
                    Toast.makeText(context, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = otp.length == 6 && !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0073E6)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Verify OTP")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(
            onClick = {
                if (canResend) {
                    resendOTP(
                        context = context,
                        navController = navController,
                        countryCode = storedCountryCode,
                        phoneNumber = storedPhoneNumber,
                        onComplete = { success ->
                            if (success) {
                                timeLeft = 60
                                canResend = false
                                otpDigits.replaceAll { "" }
                                focusRequesters[0].requestFocus()
                            }
                        }
                    )
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

    LaunchedEffect(Unit) {
        delay(300)
        focusRequesters[0].requestFocus()
    }
}


@Composable
fun OtpInput(
    otpDigits: MutableList<String>,
    focusRequesters: List<FocusRequester>,
    onOtpChanged: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        otpDigits.forEachIndexed { index, digit ->
            OutlinedTextField(
                value = digit,
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                        otpDigits[index] = newValue
                        onOtpChanged(otpDigits.joinToString(""))

                        if (newValue.isNotEmpty() && index < otpDigits.lastIndex) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                modifier = Modifier
                    .width(48.dp)
                    .focusRequester(focusRequesters[index])
                    .onKeyEvent { event ->
                        if (
                            event.type == KeyEventType.KeyDown &&
                            event.key == Key.Backspace &&
                            otpDigits[index].isEmpty() &&
                            index > 0
                        ) {
                            focusRequesters[index - 1].requestFocus()
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
