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
    private val triggerDuration = 5000L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
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
                checkJob?.cancel()
                checkJob = null
            }
        }

        return volumeUpPressed && volumeDownPressed
    }

    private fun triggerEmergencyAction() {
        Log.d("VolumeButtonService", "Emergency action triggered!")
        EmergencyHelper.sendSmsAndCall(applicationContext)
    }
}
