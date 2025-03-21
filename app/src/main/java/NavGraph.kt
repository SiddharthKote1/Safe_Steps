package com.example.chat

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}


@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context=LocalContext.current

    LaunchedEffect(Unit) {
        if (isUserLoggedIn(context)) {
            navController.navigate("MainScreen") {
                popUpTo("PhoneScreen") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "PhoneScreen") {
        composable("PhoneScreen") {
            PhoneScreen(navController)
        }
        composable("OTP") {
            val context=LocalContext.current
            OTPScreen(
                onVerifyClick = { otp ->
                    verifyPhoneNumberWithCode(context, storedVerificationId, otp, navController)
                },
                onResendClick = {
                    resendOTP(context, navController, storedCountryCode, storedPhoneNumber)
                },
                navController = navController
            )
        }
        composable("MainScreen") {
            MainScreen()
        }
    }
}


val auth = FirebaseAuth.getInstance()
var storedVerificationId = ""
var storedResendToken: PhoneAuthProvider.ForceResendingToken? = null
var storedCountryCode = ""
var storedPhoneNumber = ""

fun signInWithPhoneAuthCredential(context: Context, credential: PhoneAuthCredential, navController: NavController) {
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate("success")
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "Wrong OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

fun onLoginClicked(context: Context, navController: NavController, countryCode: String, phoneNumber: String, onCodeSend: () -> Unit) {
    auth.setLanguageCode("en")
    storedCountryCode = countryCode
    storedPhoneNumber = phoneNumber

    val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(context, p0, navController)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.d("phonebook", "Verification failed: $p0")
            Toast.makeText(context, "Verification Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            storedVerificationId = verificationId
            storedResendToken = token
            onCodeSend()
        }
    }

    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber("+$countryCode$phoneNumber")
        .setTimeout(60L, TimeUnit.SECONDS)
        .setActivity(context as Activity)
        .setCallbacks(callback)
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}

fun verifyPhoneNumberWithCode(context: Context, verificationId: String, code: String, navController: NavController) {
    val credential = PhoneAuthProvider.getCredential(verificationId, code)
    signInWithPhoneAuthCredential(context, credential, navController)
}

fun resendOTP(context: Context, navController: NavController, countryCode: String, phoneNumber: String) {
    val token = storedResendToken
    if (token != null) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+$countryCode$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(context, p0, navController)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d("phonebook", "Resend failed: $p0")
                    Toast.makeText(context, "Resend Failed", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    storedVerificationId = verificationId
                    storedResendToken = token
                    Toast.makeText(context, "OTP Resent", Toast.LENGTH_SHORT).show()
                }
            })
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    } else {
        Toast.makeText(context, "Please request OTP first", Toast.LENGTH_SHORT).show()
    }
}


