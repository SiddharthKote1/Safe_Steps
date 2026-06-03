package com.Siddharth.SafeSteps

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.Siddharth.SafeSteps.data.RetrofitClient
import com.Siddharth.SafeSteps.ttsdataclass.TtsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Floating "Hey-Google"-style SOS popup drawn over other apps.
 * Shows the live transcript + AI replies (from SosConversationState), the current threat
 * level (from ThreatLevelManager), speaks each AI reply via TTS, and offers a Stop button.
 *
 * Requires the SYSTEM_ALERT_WINDOW ("Display over other apps") permission. Started by
 * AudioStreamingService once an SOS session is live and the permission is granted.
 */
class OverlayService : Service() {

    private val TAG = "OverlayService"

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)

    private var windowManager: WindowManager? = null
    private var rootView: View? = null
    private var chatContainer: LinearLayout? = null
    private var scrollView: ScrollView? = null
    private var threatBadge: TextView? = null

    private var renderedCount = 0
    private var lastSpokenAiText: String? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Can't draw without the overlay permission — bail quietly (SMS/alerts still work).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Log.w(TAG, "Overlay permission not granted; SOS popup will not be shown.")
            stopSelf()
            return START_NOT_STICKY
        }
        if (rootView == null) {
            showOverlay()
            observeState()
        }
        return START_STICKY
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun rounded(hex: String, radiusDp: Int) = GradientDrawable().apply {
        cornerRadius = dp(radiusDp).toFloat()
        setColor(Color.parseColor(hex))
    }

    private fun showOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            (resources.displayMetrics.widthPixels * 0.92f).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(40)
        }

        rootView = buildView()
        runCatching { windowManager?.addView(rootView, params) }
            .onFailure { Log.e(TAG, "Failed to add overlay view", it) }
    }

    private fun buildView(): View {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply {
                cornerRadius = dp(22).toFloat()
                setColor(Color.parseColor("#1B1B1F"))
                setStroke(dp(2), Color.parseColor("#F85149"))
            }
        }

        val header = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val title = TextView(this).apply {
            text = "🔴 SOS Active — talking to AI"
            setTextColor(Color.WHITE)
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        threatBadge = TextView(this).apply {
            text = "LOW"
            setTextColor(Color.WHITE)
            textSize = 13f
            setPadding(dp(12), dp(4), dp(12), dp(4))
            background = rounded("#3FB950", 12)
        }
        header.addView(title)
        header.addView(threatBadge)
        card.addView(header)

        chatContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(220)
            ).apply { topMargin = dp(10); bottomMargin = dp(10) }
            addView(chatContainer)
        }
        card.addView(scrollView)

        val stopBtn = Button(this).apply {
            text = "I'm Safe — Stop SOS"
            setTextColor(Color.WHITE)
            background = rounded("#F85149", 14)
            setOnClickListener { stopSos() }
        }
        card.addView(stopBtn)

        return card
    }

    private fun observeState() {
        scope.launch {
            SosConversationState.messages.collectLatest { renderMessages(it) }
        }
        scope.launch {
            ThreatLevelManager.threatLevel.collectLatest { updateBadge(it ?: "LOW") }
        }
    }

    private fun renderMessages(msgs: List<SosConversationState.ChatMessage>) {
        val container = chatContainer ?: return
        while (renderedCount < msgs.size) {
            val m = msgs[renderedCount]
            container.addView(bubble(m))
            renderedCount++
            if (!m.fromUser && m.text != lastSpokenAiText) {
                lastSpokenAiText = m.text
                speak(m.text)
            }
        }
        scrollView?.post { scrollView?.fullScroll(View.FOCUS_DOWN) }
    }

    private fun bubble(m: SosConversationState.ChatMessage): TextView = TextView(this).apply {
        text = (if (m.fromUser) "You:  " else "AI:  ") + m.text
        setTextColor(if (m.fromUser) Color.parseColor("#C9D1D9") else Color.parseColor("#7EE787"))
        textSize = 14f
        setPadding(dp(10), dp(6), dp(10), dp(6))
        layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { topMargin = dp(4) }
    }

    private fun updateBadge(level: String) {
        val color = when (level) {
            "CRITICAL" -> "#FF4444"
            "HIGH" -> "#F85149"
            "MEDIUM" -> "#F0A030"
            else -> "#3FB950"
        }
        threatBadge?.text = level
        threatBadge?.background = rounded(color, 12)
    }

    private fun speak(text: String) {
        if (text.isBlank()) return
        scope.launch(Dispatchers.IO) {
            try {
                val resp = RetrofitClient.apiService.synthesizeSpeech(
                    TtsRequest(text = text, language = "en-IN")
                )
                val bytes = resp.bytes()
                if (bytes.isNotEmpty()) {
                    val f = File(cacheDir, "sos_ai_audio.wav")
                    FileOutputStream(f).use { it.write(bytes) }
                    withContext(Dispatchers.Main) {
                        runCatching {
                            mediaPlayer?.release()
                            mediaPlayer = MediaPlayer().apply {
                                setDataSource(f.absolutePath)
                                prepare()
                                start()
                                setOnCompletionListener { it.release() }
                            }
                        }.onFailure { Log.e(TAG, "TTS playback failed", it) }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "TTS request failed", e)
            }
        }
    }

    private fun stopSos() {
        scope.launch(Dispatchers.IO) {
            runCatching { RetrofitClient.apiService.endSession() }
        }
        stopService(Intent(this, AudioStreamingService::class.java))
        stopService(Intent(this, LocationService::class.java))
        ThreatLevelManager.clearSession()
        SosConversationState.clear()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        rootView?.let { v -> runCatching { windowManager?.removeView(v) } }
        rootView = null
    }
}
