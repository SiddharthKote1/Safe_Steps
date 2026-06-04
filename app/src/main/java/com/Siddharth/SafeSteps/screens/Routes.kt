package com.Siddharth.SafeSteps.screens

import android.net.Uri

/**
 * Builds the NeeScreen destination using query parameters. Optional fields (such as a
 * blank secondary contact) are encoded as empty query values, which Navigation tolerates,
 * instead of empty path segments, which it cannot match (causing a crash on "Proceed").
 */
fun neeScreenRoute(
    name: String?,
    countryCode1: String?,
    countryCode2: String?,
    phoneNumber1: String?,
    phoneNumber2: String?
): String =
    "NeeScreen?name=${Uri.encode(name.orEmpty())}" +
        "&countryCode1=${Uri.encode(countryCode1.orEmpty())}" +
        "&countryCode2=${Uri.encode(countryCode2.orEmpty())}" +
        "&phoneNumber1=${Uri.encode(phoneNumber1.orEmpty())}" +
        "&phoneNumber2=${Uri.encode(phoneNumber2.orEmpty())}"

object Routes {
    const val SPLASH_SCREEN = "SplashScreen"
    const val INTRO_SCREEN = "IntroScreen"
    const val LOGIN_SCREEN = "LoginScreen"
    const val REGISTER_SCREEN = "RegisterScreen"
    const val PERMISSION_SCREEN = "PermissionScreen"
    const val LOCATION_SCREEN = "location_screen"
    const val SETUP_CONTACTS = "setup_contacts"
    const val MAIN_SCREEN = "main_screen"
    const val HELP_SCREEN = "HelpScreen"
    const val REPORT_SCREEN = "ReportScreen/{sessionId}"
    const val NEE_SCREEN = "NeeScreen?name={name}&countryCode1={countryCode1}&countryCode2={countryCode2}&phoneNumber1={phoneNumber1}&phoneNumber2={phoneNumber2}"
    const val CHAT_SCREEN = "ChatScreen/{sessionId}"
    const val HISTORY_SCREEN = "HistoryScreen"
    const val ANALYTICS_SCREEN = "AnalyticsScreen"
    const val PROFILE_SCREEN = "ProfileScreen"
}