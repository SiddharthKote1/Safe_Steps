package com.Siddharth.SafeSteps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicInteger

object ThreatLevelManager {

    /** How many 20-second SMS updates use the situation-aware template before reverting. */
    const val MODIFIED_SMS_COUNT = 5

    private val _threatLevel = MutableStateFlow<String?>(null)
    val threatLevel: StateFlow<String?> = _threatLevel

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId

    /** Latest AI situation phrase used to build the modified SMS (overwritten on every update). */
    private val _situationSummary = MutableStateFlow<String?>(null)
    val situationSummary: StateFlow<String?> = _situationSummary

    /** How many more 20s SMS updates should use the situation-aware template. */
    val modifiedSmsRemaining = AtomicInteger(0)

    /** Latest threat level string (LOW / MEDIUM / HIGH / CRITICAL). */
    @Volatile
    var currentLevel: String? = null
        private set

    fun updateThreatLevel(level: String) {
        _threatLevel.value = level
        currentLevel = level
    }

    /**
     * Called for every AI assessment that arrives over the WebSocket. Each assessment corresponds
     * to a fresh spoken update from the user, so on an elevated level this overwrites the situation
     * summary and RE-ARMS the counter back to 5 (discarding any leftover cycles — the newest spoken
     * situation always wins). When the user is reported safe / LOW, it de-escalates immediately so
     * the next SMS reverts to the normal location-only message.
     */
    fun onThreatUpdate(level: String, summary: String, isSafe: Boolean) {
        _threatLevel.value = level
        currentLevel = level
        val elevated = level == "HIGH" || level == "CRITICAL" || level == "MEDIUM"
        when {
            isSafe || level == "LOW" -> {
                modifiedSmsRemaining.set(0)
                _situationSummary.value = null
            }
            elevated -> {
                if (summary.isNotBlank()) _situationSummary.value = summary
                modifiedSmsRemaining.set(MODIFIED_SMS_COUNT)
            }
        }
    }

    /** Decrements the counter; returns true if a situation-aware SMS is still owed for this cycle. */
    fun consumeOneModifiedSms(): Boolean =
        modifiedSmsRemaining.getAndUpdate { if (it > 0) it - 1 else 0 } > 0

    fun setSessionId(id: String?) {
        _activeSessionId.value = id
    }

    fun clearSession() {
        _activeSessionId.value = null
        _threatLevel.value = null
        currentLevel = null
        _situationSummary.value = null
        modifiedSmsRemaining.set(0)
    }
}
