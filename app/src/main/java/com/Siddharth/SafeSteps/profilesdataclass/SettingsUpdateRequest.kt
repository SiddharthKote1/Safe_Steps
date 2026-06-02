package com.Siddharth.SafeSteps.profilesdataclass

data class SettingsUpdateRequest(
    val notificationEnabled: Boolean,
    val privacyEnabled: Boolean,
    val themeDarkMode: Boolean,
    val sosSensitivity: Double
)
