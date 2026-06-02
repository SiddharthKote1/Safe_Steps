package com.Siddharth.SafeSteps.viewmodel

import com.Siddharth.SafeSteps.notificationdataclass.RegisterDeviceRequest
import com.Siddharth.SafeSteps.notificationdataclass.SendNotificationRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.repository.NotificationRepository

class NotificationViewModel(private val repository: NotificationRepository) : ViewModel() {

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