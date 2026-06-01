package NotificationDataClass

data class RegisterDeviceRequest(
    val deviceToken: String,
    val deviceType: String
)