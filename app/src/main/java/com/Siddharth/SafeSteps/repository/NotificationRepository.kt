package com.Siddharth.SafeSteps.repository

import com.Siddharth.SafeSteps.notificationdataclass.RegisterDeviceRequest
import com.Siddharth.SafeSteps.notificationdataclass.SendNotificationRequest
import com.Siddharth.SafeSteps.data.ApiService

class NotificationRepository(private val api: ApiService) {

    suspend fun registerDevice(
        request: RegisterDeviceRequest
    ) = api.registerDevice(request)

    suspend fun sendNotification(
        request: SendNotificationRequest
    ) = api.sendNotification(request)

    suspend fun getNotificationHistory() =
        api.getNotificationHistory()
}