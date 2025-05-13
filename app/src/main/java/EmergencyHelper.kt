import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.widget.Toast
import com.example.chat.PreferencesHelper
import androidx.core.net.toUri

object EmergencyHelper {

    var contact1: String? = null
    var contact2: String? = null

    fun sendSmsAndCall(context: Context) {
        // Check if contacts are set
        val user = PreferencesHelper(context).getUserData()
        if (user == null || contact1 == null || contact2 == null) {
            Toast.makeText(context, "No emergency contacts saved or not set.", Toast.LENGTH_SHORT).show()
            return
        }

        val message = "This is an emergency! Please help me."

        // Send SMS
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contact1, null, message, null, null)
            smsManager.sendTextMessage(contact2, null, message, null, null)
            Toast.makeText(context, "Emergency SMS sent", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Make a phone call to contact1
        try {
            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = "tel:$contact1".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(callIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to make call: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}



