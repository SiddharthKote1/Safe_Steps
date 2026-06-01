package ProfilesDataclass

data class SettingsUpdateRequest(
    val notificationEnabled: Boolean,
    val privacyEnabled: Boolean,
    val themeDarkMode: Boolean,
    val sosSensitivity: Double
)
