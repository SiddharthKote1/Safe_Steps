package com.Siddharth.SafeSteps.notificationdataclass

data class RegisterDeviceRequest(
    val deviceToken: String,
    val deviceType: String
)