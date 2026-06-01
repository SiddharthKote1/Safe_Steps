package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import repository.AnalyticsRepository

class AnalyticsViewModel : ViewModel() {

    private val repository = AnalyticsRepository()

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