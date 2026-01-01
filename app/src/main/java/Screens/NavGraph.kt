package com.Siddharth.chat

import HelpScreen
import Screens.IntroScreen
import Screens.PermissionScreen
import Screens.MainScreen
import Screens.NeeScreen
import LocationPermission
import PreferencesHelper
import android.app.Activity
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as Activity
    val preferencesHelper = PreferencesHelper(context)

    // Helper function to check if MainScreen info is filled
    fun isUserDataComplete(): Boolean {
        val user = preferencesHelper.getUserData() ?: return false
        return user.name.isNotBlank() &&
                user.phone1.isNotBlank() &&
                user.phone2.isNotBlank()
    }

    // Determine start destination
    val startDestination = when {
        !preferencesHelper.isAppSetupDone() -> Routes.INTRO_SCREEN
        !isUserDataComplete() -> Routes.MAIN_SCREEN
        else -> {
            val user = preferencesHelper.getUserData()!!
            "NeeScreen/${Uri.encode(user.name)}/${Uri.encode(user.countryCode1)}/${Uri.encode(user.countryCode2)}/${Uri.encode(user.phone1)}/${Uri.encode(user.phone2)}"
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.INTRO_SCREEN) {
            IntroScreen(navController = navController)
        }


        composable(Routes.PERMISSION_SCREEN) {
            PermissionScreen(navController = navController)
        }

        composable(Routes.LOCATION_SCREEN) {
            LocationPermission(navController = navController)
        }
        composable(Routes.HELP_SCREEN){
            HelpScreen(navController=navController)

        }
        // MainScreen for user info
        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController = navController)
        }

        composable(
            route = "NeeScreen/{name}/{countryCode1}/{countryCode2}/{phoneNumber1}/{phoneNumber2}",
            arguments = listOf(
                navArgument("name") { type = NavType.StringType },
                navArgument("countryCode1") { type = NavType.StringType },
                navArgument("countryCode2") { type = NavType.StringType },
                navArgument("phoneNumber1") { type = NavType.StringType },
                navArgument("phoneNumber2") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            NeeScreen(
                name = backStackEntry.arguments?.getString("name") ?: "",
                countryCode1 = backStackEntry.arguments?.getString("countryCode1") ?: "",
                countryCode2 = backStackEntry.arguments?.getString("countryCode2") ?: "",
                phoneNumber1 = backStackEntry.arguments?.getString("phoneNumber1") ?: "",
                phoneNumber2 = backStackEntry.arguments?.getString("phoneNumber2") ?: "",
                navController= navController
            )
        }
    }
}


/*
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
                val error = task.exception?.message ?: "Invalid OTP"
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
                .setPhoneNumber("$countryCode$phoneNumber") // ✅ no extra "+"
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
*/