package com.Siddharth.SafeSteps

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.*

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeDownPressed = false
    private var checkJob: Job? = null
    private val triggerDuration = 5000L // 5 seconds long press

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("VolumeButtonService", "Key event: ${event.keyCode}, Action: ${event.action}")

        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> handleVolumeKey(event)
            else -> super.onKeyEvent(event)
        }
    }

    private fun handleVolumeKey(event: KeyEvent): Boolean {
        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (!volumeDownPressed) {
                    volumeDownPressed = true
                    checkJob?.cancel()
                    checkJob = CoroutineScope(Dispatchers.Default).launch {
                        delay(triggerDuration)
                        if (volumeDownPressed) {
                            triggerEmergencyAction()
                        }
                    }
                }
            }

            KeyEvent.ACTION_UP -> {
                volumeDownPressed = false
                checkJob?.cancel()
                checkJob = null
            }
        }

        return false // Do not consume the event, let the user change media volume!
    }

    private fun triggerEmergencyAction() {
        Log.d("VolumeButtonService", "Emergency action triggered!")
        EmergencyHelper.sendSmsAndCall(applicationContext)
    }
}
