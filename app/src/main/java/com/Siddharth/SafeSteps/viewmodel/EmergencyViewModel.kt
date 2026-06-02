package com.Siddharth.SafeSteps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.repository.EmergencyRepository

class EmergencyViewModel(private val repository: EmergencyRepository) : ViewModel() {

    fun startSession() {

        viewModelScope.launch {

            repository.startSession()
        }
    }

    fun endSession() {

        viewModelScope.launch {

            repository.endSession()
        }
    }

    fun triggerSos() {

        viewModelScope.launch {

            repository.triggerSos()
        }
    }

    fun getHistory() {

        viewModelScope.launch {

            repository.getHistory()
        }
    }

    fun getIncidents() {

        viewModelScope.launch {

            repository.getIncidents()
        }
    }
}