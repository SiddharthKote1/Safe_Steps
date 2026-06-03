package com.Siddharth.SafeSteps

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Live chat between the user and the AI during an active SOS session.
 * AudioStreamingService feeds it from the WebSocket (user transcript + AI guidance),
 * and OverlayService renders + speaks it. Cleared when the SOS ends.
 */
object SosConversationState {

    data class ChatMessage(val text: String, val fromUser: Boolean)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    fun addUser(text: String) = append(text, fromUser = true)

    fun addAi(text: String) = append(text, fromUser = false)

    private fun append(text: String, fromUser: Boolean) {
        val t = text.trim()
        if (t.isEmpty()) return
        // Skip an exact consecutive duplicate from the same speaker.
        val last = _messages.value.lastOrNull()
        if (last != null && last.fromUser == fromUser && last.text == t) return
        _messages.value = _messages.value + ChatMessage(t, fromUser)
    }

    fun clear() {
        _messages.value = emptyList()
    }
}
