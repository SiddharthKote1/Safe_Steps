package viewmodel

import NotificationDataClass.RegisterDeviceRequest
import NotificationDataClass.SendNotificationRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.NotificationRepository

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()

    fun registerDevice(
        request: RegisterDeviceRequest
    ) {

        viewModelScope.launch {

            repository.registerDevice(request)
        }
    }

    fun sendNotification(
        request: SendNotificationRequest
    ) {

        viewModelScope.launch {

            repository.sendNotification(request)
        }
    }

    fun getNotificationHistory() {

        viewModelScope.launch {

            repository.getNotificationHistory()
        }
    }
}