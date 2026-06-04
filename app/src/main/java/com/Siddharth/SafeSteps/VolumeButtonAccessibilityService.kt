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
    private val triggerDuration = 5000L // hold BOTH volume buttons for 5 seconds

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not used
    }

    override fun onInterrupt() {
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d("VolumeButtonService", "Key event: ${event.keyCode}, Action: ${event.action}")

        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> when (event.action) {
                KeyEvent.ACTION_DOWN -> volumeUpPressed = true
                KeyEvent.ACTION_UP -> volumeUpPressed = false
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> when (event.action) {
                KeyEvent.ACTION_DOWN -> volumeDownPressed = true
                KeyEvent.ACTION_UP -> volumeDownPressed = false
            }

            else -> return super.onKeyEvent(event)
        }

        evaluateCombo()

        // Consume the event only while BOTH buttons are held, so the system volume
        // dialog stays hidden during the 5-second SOS gesture. A single button still
        // adjusts the volume normally.
        return volumeUpPressed && volumeDownPressed
    }

    private fun evaluateCombo() {
        if (volumeUpPressed && volumeDownPressed) {
            // Both buttons are now held — start the 5s countdown once.
            if (checkJob == null) {
                checkJob = CoroutineScope(Dispatchers.Default).launch {
                    delay(triggerDuration)
                    if (volumeUpPressed && volumeDownPressed) {
                        triggerEmergencyAction()
                    }
                }
            }
        } else {
            // A button was released before 5s elapsed — abort.
            checkJob?.cancel()
            checkJob = null
        }
    }

    private fun triggerEmergencyAction() {
        Log.d("VolumeButtonService", "Emergency action triggered!")
        EmergencyHelper.sendSmsAndCall(applicationContext)
    }
}
