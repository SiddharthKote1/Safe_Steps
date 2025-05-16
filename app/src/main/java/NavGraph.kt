package com.example.chat

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.RoomDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
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
    val context = LocalContext.current
    val activity = context as Activity

    val googleSignInHelper = GoogleSignInHelper(context)
    val account = GoogleSignIn.getLastSignedInAccount(context)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                Toast.makeText(context, "Welcome ${account.email}", Toast.LENGTH_SHORT).show()
                navController.navigate("HomeScreen") {
                    popUpTo("LoginScreen") { inclusive = true }
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Sign-in canceled", Toast.LENGTH_SHORT).show()
        }
    }

    val currentUser = auth.currentUser
    val lastSignedInAccount = GoogleSignIn.getLastSignedInAccount(context)
    val startDestination = if (currentUser != null || lastSignedInAccount != null) {
        Routes.MAIN_SCREEN
    } else {
        Routes.PHONE_SCREEN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.PHONE_SCREEN) {
            PhoneScreen(navController)
        }
        composable(Routes.OTP_SCREEN) {
            OTPScreen(navController)
        }
        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController, user=auth.currentUser)
        }
        composable(
            route = "NeeScreen/{name}/{code1}/{code2}/{phone1}/{phone2}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("code1") { type = NavType.StringType },
                navArgument("code2") { type = NavType.StringType },
                navArgument("phone1") { type = NavType.StringType },
                navArgument("phone2") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: ""
            val code1 = backStackEntry.arguments?.getString("code1") ?: ""
            val code2 = backStackEntry.arguments?.getString("code2") ?: ""
            val phone1 = backStackEntry.arguments?.getString("phone1") ?: ""
            val phone2 = backStackEntry.arguments?.getString("phone2") ?: ""

            NeeScreen(
                user = auth.currentUser,
                name = name,
                countryCode1 = code1,
                countryCode2 = code2,
                phoneNumber1 = phone1,
                phoneNumber2 = phone2
            )
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