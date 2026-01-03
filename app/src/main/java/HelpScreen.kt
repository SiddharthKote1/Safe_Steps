package com.Siddharth.SafeSteps

import Screens.BottomBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController) {

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 48.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Text(
                    text = "Help & Usage Guide",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Learn how to use SafeSteps effectively",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                HelpSection(
                    title = "Permissions Required",
                    points = listOf(
                        "Allow Phone & SMS permissions to send alerts.",
                        "Enable Location access (Fine & Coarse).",
                        "Turn on Accessibility Service for emergency detection."
                    )
                )

                HelpSection(
                    title = "Emergency Contacts",
                    points = listOf(
                        "Add at least one emergency contact.",
                        "Two contacts are recommended for reliability.",
                        "Ensure contacts can receive calls & SMS."
                    )
                )

                HelpSection(
                    title = "Sending an Emergency Alert",
                    points = listOf(
                        "Press and hold Volume Up + Volume Down for 5 seconds.",
                        "App will automatically place a call.",
                        "Location link is sent via SMS."
                    )
                )

                HelpSection(
                    title = "Location Updates",
                    points = listOf(
                        "Location updates run in background.",
                        "Keep GPS enabled for accuracy."
                    )
                )

                HelpSection(
                    title = "Tips for Reliable Usage",
                    points = listOf(
                        "Disable battery optimization for SafeSteps.",
                        "Keep Accessibility Service always enabled.",
                        "Test alerts with trusted contacts."
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun HelpSection(
    title: String,
    points: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            points.forEach {
                Text(
                    text = "• $it",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    MaterialTheme {
        Surface {
            HelpScreen(navController = NavController(LocalContext.current))
        }
    }
}
