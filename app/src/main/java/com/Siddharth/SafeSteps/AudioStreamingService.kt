package com.Siddharth.SafeSteps

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.Siddharth.SafeSteps.data.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString

class AudioStreamingService : Service() {

    private val TAG = "AudioStreamingService"
    private val CHANNEL_ID = "SafeStepsAudioChannel"

    private var audioRecord: AudioRecord? = null
    private var webSocket: WebSocket? = null
    private var isRecording = false
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sessionId = intent?.getStringExtra("SESSION_ID") ?: return START_NOT_STICKY

        createNotificationChannel()
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Emergency SOS Active")
            .setContentText("Streaming audio to emergency contacts...")
            //.setSmallIcon(R.drawable.safes) // Replace with a valid icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        startForeground(1, notification)

        // Launch the floating SOS assistant popup (live transcript + AI voice replies),
        // if the user has granted the "Display over other apps" permission. Starting it from
        // this already-foreground service avoids background-start restrictions.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || android.provider.Settings.canDrawOverlays(this)) {
            startService(Intent(this, OverlayService::class.java))
        }

        startStreaming(sessionId)

        return START_STICKY
    }

    private fun startStreaming(sessionId: String) {
        val client = OkHttpClient()
        // wss:// (TLS) — the backend is served over HTTPS on Render; plain ws:// fails the upgrade.
        val url = "wss://safesteps-backend-douj.onrender.com/ws/audio/$sessionId"
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d(TAG, "WebSocket Opened")
                startAudioRecord(webSocket)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Message from Server: $text")
                try {
                    val json = org.json.JSONObject(text)

                    // Feed the live SOS chat (shown + spoken by the overlay):
                    // the user's transcribed words, then the AI's guidance reply.
                    val transcript = json.optString("transcript", "")
                    if (transcript.isNotBlank()) {
                        com.Siddharth.SafeSteps.SosConversationState.addUser(transcript)
                    }
                    val guidance = json.optString("guidance", "")
                    if (guidance.isNotBlank()) {
                        com.Siddharth.SafeSteps.SosConversationState.addAi(guidance)
                    }

                    if (json.has("threat_level")) {
                        val level = json.optString("threat_level", "LOW")
                        val isSafe = json.optBoolean("is_safe", false)

                        // Build a short situation summary for the SMS.
                        // Prefer a backend-provided sms_summary; else use reasons; else incident_type.
                        val smsSummary = json.optString("sms_summary", "")
                        val reasons = json.optString("reasons", "")
                        val incident = json.optString("incident_type", "")
                        val genericIncident = incident.isBlank() ||
                            incident.equals("None", true) || incident.equals("Unknown", true)
                        val raw = when {
                            smsSummary.isNotBlank() -> smsSummary
                            reasons.isNotBlank() -> reasons
                            !genericIncident -> incident
                            else -> ""
                        }
                        // Keep it SMS-friendly (~140 chars).
                        val summary = if (raw.length > 140) raw.take(137).trimEnd() + "…" else raw

                        com.Siddharth.SafeSteps.ThreatLevelManager.onThreatUpdate(level, summary, isSafe)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse websocket message", e)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e(TAG, "WebSocket Failure", t)
                stopSelf()
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "WebSocket Closed")
                stopSelf()
            }
        })
    }

    private fun startAudioRecord(webSocket: WebSocket) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Microphone permission not granted")
            stopSelf()
            return
        }

        val sampleRate = 16000
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        // 256ms chunk size as per backend requirements
        val chunkSamples = 4096 
        val bufferSize = chunkSamples * 2 // 16-bit = 2 bytes

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            if (minBufSize > bufferSize) minBufSize else bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        scope.launch {
            val buffer = ByteArray(bufferSize)
            while (isActive && isRecording) {
                val readResult = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readResult > 0) {
                    webSocket.send(buffer.toByteString(0, readResult))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        webSocket?.close(1000, "Service destroyed")
        // Don't leave the SOS popup floating if audio streaming ends.
        stopService(Intent(this, OverlayService::class.java))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Emergency Audio Streaming",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
