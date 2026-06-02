package com.Siddharth.SafeSteps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ThreatLevelManager {
    private val _threatLevel = MutableStateFlow<String?>(null)
    val threatLevel: StateFlow<String?> = _threatLevel
    
    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId

    fun updateThreatLevel(level: String) {
        _threatLevel.value = level
    }

    fun setSessionId(id: String?) {
        _activeSessionId.value = id
    }

    fun clearSession() {
        _activeSessionId.value = null
        _threatLevel.value = null
    }
}
