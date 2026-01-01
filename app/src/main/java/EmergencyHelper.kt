package com.Siddharth.SafeSteps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import PreferencesHelper
import kotlinx.coroutines.*

object EmergencyHelper {

    var contact1: String? = null
    var contact2: String? = null

    fun sendSmsAndCall(context: Context) {
        val user = PreferencesHelper(context).getUserData()
        if (user == null || contact1.isNullOrEmpty() || contact2.isNullOrEmpty()) {
            Toast.makeText(context, "User data or contacts not available", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val locationReady = waitForLocation()
            if (!locationReady) {
                Toast.makeText(context, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val lat = LocationHolder.latitude
            val lng = LocationHolder.longitude
            val locationUrl = "https://maps.google.com/?q=$lat,$lng"
            val message = " \"EMERGENCY! I${user.name}. My location: $locationUrl\"  \n"

            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(contact1, null, message, null, null)
                smsManager.sendTextMessage(contact2, null, message, null, null)
                Toast.makeText(context, "Emergency SMS sent", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "SMS failed: ${e.message}", Toast.LENGTH_LONG).show()
            }

            // Make emergency call
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$contact1"))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else if (context is Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    1
                )
            }
        }
    }

    private suspend fun waitForLocation(timeoutMillis: Long = 5000): Boolean {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMillis) {
            if (LocationHolder.latitude != null && LocationHolder.longitude != null) return true
            delay(500)
        }
        return false
    }
}
