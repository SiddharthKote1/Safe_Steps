package com.Siddharth.SafeSteps.screens

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.conversiondataclass.ConversationRequest
import com.Siddharth.SafeSteps.data.RetrofitClient
import com.Siddharth.SafeSteps.ttsdataclass.TtsRequest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class ChatMessage(val text: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    navController: NavController
) {
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Safety Assistant", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FC)
                )
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Describe your situation...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            val userMsg = inputText
                            inputText = ""
                            messages = messages + ChatMessage(userMsg, isUser = true)
                            isLoading = true
                            
                            coroutineScope.launch {
                                try {
                                    val response = RetrofitClient.apiService.sendMessage(
                                        ConversationRequest(message = userMsg, language = "en-IN")
                                    )
                                    messages = messages + ChatMessage(response.guidance, isUser = false)
                                    
                                    // Handle TTS
                                    try {
                                        val ttsResponse = RetrofitClient.apiService.synthesizeSpeech(
                                            TtsRequest(text = response.guidance, language = "en-IN")
                                        )
                                        
                                        val bytes = ttsResponse.bytes()
                                        if (bytes.isNotEmpty()) {
                                            val tempFile = File(context.cacheDir, "ai_audio.wav")
                                            val fos = FileOutputStream(tempFile)
                                            fos.write(bytes)
                                            fos.close()
                                            
                                            val mediaPlayer = MediaPlayer()
                                            mediaPlayer.setDataSource(tempFile.absolutePath)
                                            mediaPlayer.prepare()
                                            mediaPlayer.start()
                                            mediaPlayer.setOnCompletionListener {
                                                it.release()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    
                                } catch (e: Exception) {
                                    messages = messages + ChatMessage("Failed to get response: ${e.message}", isUser = false)
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            reverseLayout = true
        ) {
            items(messages.reversed()) { msg ->
                ChatBubble(msg)
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val backgroundColor = if (msg.isUser) MaterialTheme.colorScheme.primary else Color(0xFFE0E0E0)
    val textColor = if (msg.isUser) Color.White else Color.Black
    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .background(
                    backgroundColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = msg.text, color = textColor)
        }
    }
}
