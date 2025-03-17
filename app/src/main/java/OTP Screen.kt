package com.example.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip

@Composable
fun OTPScreen(onVerifyClick: (String) -> Unit) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Enter OTP", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            otpValues.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        //.clip(RoundedCornerShape(8.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                otpValues = otpValues.toMutableList().also { it[index] = newValue }
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = LocalTextStyle.current.copy(fontSize = 20.sp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onVerifyClick(otpValues.joinToString("")) },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0073E6))
        ) {
            Text(text = "Verify", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTPScreenPreview() {
    OTPScreen { otp -> println("Entered OTP: $otp") }
}


