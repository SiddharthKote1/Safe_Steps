package com.example.chat

import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.net.Uri
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object EmergencyHelper {

    var contact1: String? = null
    var contact2: String? = null

    fun sendSmsAndCall(context: Context) {
        val user = PreferencesHelper(context).getUserData()
        if (user == null || contact1.isNullOrEmpty() || contact2.isNullOrEmpty()) {
            Toast.makeText(context, "No emergency contacts saved or not set.", Toast.LENGTH_SHORT).show()
            return
        }

        val message = "This is an emergency! Please help me. My name is ${user.name}"

        // Send SMS
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contact1, null, message, null, null)
            smsManager.sendTextMessage(contact2, null, message, null, null)
            Toast.makeText(context, "Emergency SMS sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Make the call directly
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            // Proceed with the call if permission is granted
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$contact1")
            }
            context.startActivity(callIntent)
        } else {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(
                context as android.app.Activity,
                arrayOf(android.Manifest.permission.CALL_PHONE),
                1 // Request code for CALL_PHONE permission
            )
        }
    }
}
