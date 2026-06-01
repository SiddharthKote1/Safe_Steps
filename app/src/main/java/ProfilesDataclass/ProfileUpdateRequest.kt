package ProfilesDataclass

data class ProfileUpdateRequest(
    val age: String,
    val bloodGroup: String,
    val dateOfBirth: String,
    val fullName: String,
    val gender: String,
    val medicalNotes: String,
    val notificationEnabled: Boolean,
    val preferredLanguage: String,
    val privacyEnabled: Boolean,
    val sosSensitivity: Int,
    val themeDarkMode: Boolean
)