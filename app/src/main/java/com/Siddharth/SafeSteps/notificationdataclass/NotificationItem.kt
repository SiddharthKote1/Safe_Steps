package com.Siddharth.SafeSteps.notificationdataclass

data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val sent_at: String,
    val status: String
)
