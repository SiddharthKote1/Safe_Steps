package com.Siddharth.SafeSteps

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.*

class VolumeButtonAccessibilityService : AccessibilityService() {

    private var volumeUpPressed = false
    private var volumeDownPressed = false
    private var checkJob: Job? = null
    private val triggerDuration = 5000L // 5 seconds

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
        // Handle interrupts if needed
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("VolumeButtonService", "Key event: ${event.keyCode}, Action: ${event.action}")

        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> handleVolumeKey(event, isUp = true)
            KeyEvent.KEYCODE_VOLUME_DOWN -> handleVolumeKey(event, isUp = false)
            else -> super.onKeyEvent(event)
        }
    }

    private fun handleVolumeKey(event: KeyEvent, isUp: Boolean): Boolean {
        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (isUp) volumeUpPressed = true else volumeDownPressed = true

                // If both buttons pressed, start SOS countdown
                if (volumeUpPressed && volumeDownPressed && checkJob == null) {
                    checkJob = CoroutineScope(Dispatchers.Default).launch {
                        delay(triggerDuration)
                        if (volumeUpPressed && volumeDownPressed) {
                            triggerEmergencyAction()
                        }
                    }
                }
            }

            KeyEvent.ACTION_UP -> {
                if (isUp) volumeUpPressed = false else volumeDownPressed = false
                // Cancel SOS if buttons released before time
                checkJob?.cancel()
                checkJob = null
            }
        }

        // ✅ Let the system handle normal volume changes unless *both* buttons are held
        // Returning false means "don’t consume" → volume can change
        // We only return true if both are still pressed to suppress further events once SOS triggers
        return volumeUpPressed && volumeDownPressed
    }

    private fun triggerEmergencyAction() {
        Log.d("VolumeButtonService", "🚨 Emergency action triggered!")
        // Call your helper for SOS (send SMS, call, etc.)
        EmergencyHelper.sendSmsAndCall(applicationContext)
    }
}
