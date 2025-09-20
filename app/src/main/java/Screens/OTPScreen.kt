/*
package Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
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
import com.example.SafeSteps.R

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

    // Timer effect for OTP resend
    LaunchedEffect(timeLeft, canResend) {
        if (!canResend && timeLeft > 0) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            canResend = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0052D4),
                        Color(0xFF6FB1FC),
                        Color(0xFF0052D4)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            LottieAnimation(
                composition,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.30f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.padding(start = 15.dp),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    text = "Verify Phone number",
                    fontSize = 30.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                // OTP Input
                OtpInput(
                    otpDigits = otpDigits,
                    focusRequesters = focusRequesters,
                    onOtpChanged = { updatedOtp -> otp = updatedOtp }
                )

                Spacer(modifier = Modifier.height(30.dp))

                // ✅ OTP Verification Button
                TextButton(
                    onClick = {
                        if (otp.length == 6) {
                            isLoading = true
                            verifyPhoneNumberWithCode(
                                context = context,
                                verificationId = storedVerificationId, // global var from NavGraph
                                code = otp,
                                navController = navController
                            ) { success ->
                                isLoading = false
                                if (success) {
                                    navController.navigate(Routes.MAIN_SCREEN) {
                                        popUpTo(Routes.OTP_SCREEN) { inclusive = true }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid OTP, try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    otpDigits.replaceAll { "" }
                                    focusRequesters[0].requestFocus()
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter 6-digit OTP",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = otp.length == 6 && !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Didn't get OTP?", color = Color.Black, fontSize = 16.sp)
                            TextButton(
                                onClick = {
                                    if (timeLeft > 30) {
                                        Toast.makeText(
                                            context,
                                            "Please wait before resending OTP",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else if (canResend) {
                                        resendOTP(
                                            context = context,
                                            navController = navController,
                                            countryCode = storedCountryCode,
                                            phoneNumber = storedPhoneNumber
                                        ) { success ->
                                            if (success) {
                                                timeLeft = 60
                                                canResend = false
                                                otpDigits.replaceAll { "" }
                                                focusRequesters[0].requestFocus()
                                            }
                                        }
                                    }
                                },
                                enabled = true
                            ) {
                                Text(
                                    text = "Resend OTP",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = {
                        navController.navigate(Routes.PHONE_SCREEN)
                    }) {
                        Text("Change Phone number", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // Auto focus first OTP box
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
    val boxCount = otpDigits.size
    val spacing = 12.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalSpacing = spacing * (boxCount - 1)
    val boxWidth = ((screenWidth - totalSpacing - 64.dp) / boxCount).coerceAtLeast(45.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        otpDigits.forEachIndexed { index, digit ->
            UnderlinedDigit(
                value = digit,
                modifier = Modifier
                    .width(boxWidth)
                    .height(boxWidth)
                    .focusRequester(focusRequesters[index]),
                onValueChange = { newValue ->
                    if (newValue.length <= 1 && (newValue.isEmpty() || newValue.all { it.isDigit() })) {
                        otpDigits[index] = newValue
                        onOtpChanged(otpDigits.joinToString(""))
                        if (newValue.isNotEmpty() && index < otpDigits.lastIndex) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    }
                },
                onBackspace = {
                    if (index > 0) {
                        focusRequesters[index - 1].requestFocus()
                    }
                }
            )
            if (index != otpDigits.lastIndex) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnderlinedDigit(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onBackspace: (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.onKeyEvent {
            if (it.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_DEL && value.isEmpty()) {
                onBackspace?.invoke()
                true
            } else {
                false
            }
        },
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Black,
            unfocusedIndicatorColor = Color.Black,
            cursorColor = Color.Black
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OTPScreenPreview() {
    val navController = rememberNavController()
    OTPScreen(navController = navController)
}
*/