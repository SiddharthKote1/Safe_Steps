package com.example.chat

import android.accessibilityservice.AccessibilityService
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeUpPressed = false
    private var volumeDownPressed = false
    private var pressStartTime: Long = 0L
    private val triggerDuration = 5000L // 5 seconds

    private var checkJob: Job? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                handleKeyEvent(event, isUp = true)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                handleKeyEvent(event, isUp = false)
                return true
            }
        }
        return super.onKeyEvent(event)
    }

    private fun handleKeyEvent(event: KeyEvent, isUp: Boolean) {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (isUp) volumeUpPressed = true else volumeDownPressed = true

            if (volumeUpPressed && volumeDownPressed && checkJob == null) {
                pressStartTime = System.currentTimeMillis()
                checkJob = CoroutineScope(Dispatchers.Default).launch {
                    delay(triggerDuration)
                    if (volumeUpPressed && volumeDownPressed) {
                        triggerEmergencyAction()
                    }
                }
            }
        } else if (event.action == KeyEvent.ACTION_UP) {
            if (isUp) volumeUpPressed = false else volumeDownPressed = false
            checkJob?.cancel()
            checkJob = null
        }
    }

    private fun triggerEmergencyAction() {
        // Call or send SMS using a helper function
        EmergencyHelper.sendSmsAndCall(applicationContext)
    }
}