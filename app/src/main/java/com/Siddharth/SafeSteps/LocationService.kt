package com.Siddharth.SafeSteps

import com.Siddharth.SafeSteps.locationdataclass.LocationUpdateRequest
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import com.Siddharth.SafeSteps.repository.LocationRepository
import java.util.Locale
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationService : Service(), KoinComponent {

    companion object {
        const val CHANNEL_ID = "location_channel"
    }

    private val locationRepository: LocationRepository by inject()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var smsJob: Job? = null

    private val fusedLocationProviderClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000
        ).build()
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation ?: return

                LocationHolder.latitude = location.latitude
                LocationHolder.longitude = location.longitude

                scope.launch {
                    try {
                        locationRepository.updateLocation(
                            LocationUpdateRequest(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy.toDouble(),
                                speed = location.speed.toDouble(),
                                heading = location.bearing.toDouble()
                            )
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    
                    val address = getAddressFromLocation(
                        location.latitude,
                        location.longitude
                    )

                    createNotification(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        address
                    )
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Build and show initial notification immediately to satisfy Android background requirements
        val initialNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("SafeSteps Location Tracking")
            .setContentText("Emergency tracking active. Fetching GPS coordinates...")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
        
        startForeground(1, initialNotification)

        startLocationUpdates()
        startSmsLoop()

        return START_STICKY
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun startSmsLoop() {
        smsJob?.cancel()
        smsJob = scope.launch {
            while (isActive) {
                delay(20000) // Trigger every 20 seconds
                sendEmergencySms()
            }
        }
    }

    private fun sendEmergencySms() {
        val lat = LocationHolder.latitude
        val lng = LocationHolder.longitude
        if (lat == null || lng == null) return

        val preferencesHelper = PreferencesHelper(applicationContext)
        val user = preferencesHelper.getUserData()
        val c1 = EmergencyHelper.contact1 ?: (user?.countryCode1.orEmpty() + user?.phone1.orEmpty())
        val c2 = EmergencyHelper.contact2 ?: (user?.countryCode2.orEmpty() + user?.phone2.orEmpty())

        if (c1.isNotEmpty() || c2.isNotEmpty()) {
            val msg = "EMERGENCY UPDATE! I am in danger. Latest location: https://maps.google.com/?q=$lat,$lng"
            try {
                val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    applicationContext.getSystemService(SmsManager::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    SmsManager.getDefault()
                }
                if (c1.isNotEmpty()) {
                    smsManager.sendTextMessage(c1, null, msg, null, null)
                }
                if (c2.isNotEmpty()) {
                    smsManager.sendTextMessage(c2, null, msg, null, null)
                }
                android.util.Log.d("LocationService", "Sent periodic 20s SMS update to emergency contacts.")
            } catch (e: Exception) {
                android.util.Log.e("LocationService", "Failed to send SMS fallback: ${e.message}")
            }
        }
    }

    private fun getAddressFromLocation(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                listOfNotNull(
                    address.getAddressLine(0),
                    address.locality,
                    address.adminArea,
                    address.countryName
                ).joinToString(", ")
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            "Unable to get address"
        }
    }

    private fun createNotification(lat: String, lng: String, address: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setContentTitle("Location Update")
            .setContentText("Lat: $lat, Lng: $lng")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Lat: $lat\nLng: $lng\n$address")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(ContextCompat.getColor(this, R.color.white))
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        smsJob?.cancel()
        smsJob = null
        scope.cancel()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}