package repository

import NotificationDataClass.RegisterDeviceRequest
import NotificationDataClass.SendNotificationRequest
import data.RetrofitClient

class NotificationRepository {

    private val api = RetrofitClient.apiService

    suspend fun registerDevice(
        request: RegisterDeviceRequest
    ) = api.registerDevice(request)

    suspend fun sendNotification(
        request: SendNotificationRequest
    ) = api.sendNotification(request)

    suspend fun getNotificationHistory() =
        api.getNotificationHistory()
}