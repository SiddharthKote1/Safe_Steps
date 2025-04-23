package com.example.chat

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

// Firebase authentication state
var storedVerificationId: String = ""
var storedResendToken: PhoneAuthProvider.ForceResendingToken? = null
var storedCountryCode: String = ""
var storedPhoneNumber: String = ""

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN_SCREEN
            /*if (auth.currentUser == null) Routes.PHONE_SCREEN else Routes.MAIN_SCREEN,*/
    ) {
        composable(Routes.PHONE_SCREEN) {
            PhoneScreen(navController)
        }
        composable(Routes.OTP_SCREEN) {
            OTPScreen(navController)
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController)
        }
    }
}

fun signInWithPhoneAuthCredential(
    context: Context,
    credential: PhoneAuthCredential,
    navController: NavController,
    onComplete: (Boolean) -> Unit = {}
) {
    FirebaseAuth.getInstance().signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.MAIN_SCREEN) {
                    popUpTo(Routes.PHONE_SCREEN) { inclusive = true }
                }
                onComplete(true)
            } else {
                val error = task.exception?.message ?: "Authentication failed"
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                onComplete(false)
            }
        }
}

fun onLoginClicked(
    context: Context,
    navController: NavController,
    countryCode: String,
    phoneNumber: String,
    onCodeSend: () -> Unit
) {
    val fullPhoneNumber = "+$countryCode$phoneNumber"

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(context, credential, navController)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.e("Auth", "Verification failed: ${e.message}")
            Toast.makeText(
                context,
                "Verification failed: ${e.message ?: "Unknown error"}",
                Toast.LENGTH_LONG
            ).show()
            onCodeSend()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            storedVerificationId = verificationId
            storedResendToken = token
            storedCountryCode = countryCode
            storedPhoneNumber = phoneNumber
            navController.navigate(Routes.OTP_SCREEN)
            onCodeSend()
        }
    }

    try {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(fullPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    } catch (e: Exception) {
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        onCodeSend()
    }
}

fun verifyPhoneNumberWithCode(
    context: Context,
    verificationId: String,
    code: String,
    navController: NavController,
    onComplete: (Boolean) -> Unit = {}
) {
    try {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(context, credential, navController, onComplete)
    } catch (e: Exception) {
        Toast.makeText(context, "Invalid OTP format", Toast.LENGTH_SHORT).show()
        onComplete(false)
    }
}

fun resendOTP(
    context: Context,
    navController: NavController,
    countryCode: String,
    phoneNumber: String,
    onComplete: (Boolean) -> Unit = {}
) {
    storedResendToken?.let { token ->
        try {
            val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+$countryCode$phoneNumber")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context as Activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(context, credential, navController)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(context, "Resend failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        onComplete(false)
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        storedVerificationId = verificationId
                        storedResendToken = token
                        Toast.makeText(context, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                        onComplete(true)
                    }
                })
                .setForceResendingToken(token)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            onComplete(false)
        }
    } ?: run {
        Toast.makeText(context, "Request new OTP first", Toast.LENGTH_SHORT).show()
        onComplete(false)
    }
}