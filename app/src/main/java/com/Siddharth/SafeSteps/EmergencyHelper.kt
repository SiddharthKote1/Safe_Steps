package com.Siddharth.SafeSteps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

object EmergencyHelper {

    var contact1: String? = null
    var contact2: String? = null

    fun sendSmsAndCall(context: Context) {
        val user = PreferencesHelper(context).getUserData()
        
        val c1 = contact1 ?: (user?.countryCode1.orEmpty() + user?.phone1.orEmpty())
        val c2 = contact2 ?: (user?.countryCode2.orEmpty() + user?.phone2.orEmpty())
        
        if (user == null || c1.isEmpty()) {
            Toast.makeText(context, "User data or primary contact not configured", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure LocationService is started as foreground service so recurring SMS can work
        val locationIntent = Intent(context, LocationService::class.java)
        ContextCompat.startForegroundService(context, locationIntent)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Start Session
                val sessionResponse = com.Siddharth.SafeSteps.data.RetrofitClient.apiService.startSession()
                val sessionId = sessionResponse.session_id
                com.Siddharth.SafeSteps.ThreatLevelManager.setSessionId(sessionId)
                com.Siddharth.SafeSteps.ThreatLevelManager.updateThreatLevel("LOW") // Default
                
                // 2. Start Audio Streaming Service
                val serviceIntent = Intent(context, AudioStreamingService::class.java).apply {
                    putExtra("SESSION_ID", sessionId)
                }
                ContextCompat.startForegroundService(context, serviceIntent)

                // 3. Wait for Location
                val locationReady = waitForLocation()
                if (!locationReady) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Unable to fetch location.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // 4. Update Location & Get Maps Link
                val lat = LocationHolder.latitude ?: 0.0
                val lng = LocationHolder.longitude ?: 0.0
                val locationResponse = com.Siddharth.SafeSteps.data.RetrofitClient.apiService.updateLocation(
                    com.Siddharth.SafeSteps.locationdataclass.LocationUpdateRequest(
                        latitude = lat,
                        longitude = lng,
                        accuracy = 5.0,
                        speed = 0.0,
                        heading = 0.0
                    )
                )

                val locationUrl = locationResponse.maps_link ?: "https://maps.google.com/?q=$lat,$lng"
                val message = "EMERGENCY! I am ${user.name}. My location: $locationUrl"

                // 5. Send SMS Local Fallback
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    context.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                if (c1.isNotEmpty()) {
                    smsManager.sendTextMessage(c1, null, message, null, null)
                }
                if (c2.isNotEmpty()) {
                    smsManager.sendTextMessage(c2, null, message, null, null)
                }
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Emergency SOS Active & SMS sent", Toast.LENGTH_SHORT).show()
                }

                // 6. Make emergency call
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$c1"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.CALL_PHONE),
                        1
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to start emergency session: ${e.message}", Toast.LENGTH_LONG).show()
                }
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
