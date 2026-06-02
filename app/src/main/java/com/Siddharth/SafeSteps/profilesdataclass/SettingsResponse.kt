package com.Siddharth.SafeSteps.profilesdataclass

data class SettingsResponse(
    val notificationEnabled: Boolean,
    val privacyEnabled: Boolean,
    val sosSensitivity: Double,
    val themeDarkMode: Boolean
)