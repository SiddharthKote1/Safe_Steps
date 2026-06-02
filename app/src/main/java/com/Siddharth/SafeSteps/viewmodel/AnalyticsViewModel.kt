package com.Siddharth.SafeSteps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Siddharth.SafeSteps.repository.AnalyticsRepository

class AnalyticsViewModel(private val repository: AnalyticsRepository) : ViewModel() {

    fun getAnalytics() {

        viewModelScope.launch {

            repository.getAnalytics()
        }
    }

    fun getOverview() {

        viewModelScope.launch {

            repository.getAnalyticsOverview()
        }
    }

    fun getTrends() {

        viewModelScope.launch {

            repository.getTrends()
        }
    }
}