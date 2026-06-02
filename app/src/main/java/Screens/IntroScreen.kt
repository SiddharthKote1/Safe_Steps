package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.Siddharth.SafeSteps.R

@Composable
fun IntroScreen(
    navController: NavController
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                painter = painterResource(R.drawable.safes),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to",
                fontSize = 18.sp,
                color = Color.DarkGray
            )

            Text(
                text = "SafeSteps",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your personal safety companion designed to protect you anytime, anywhere.",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            FeatureCard(
                icon = {
                    Icon(
                        Icons.Default.Notifications,
                        null,
                        tint = Color(0xFFFF5B6A)
                    )
                },
                title = "One-tap SOS",
                subtitle = "Instant help in emergencies"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureCard(
                icon = {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        tint = Color(0xFF10B981)
                    )
                },
                title = "Live Location",
                subtitle = "Share your real-time location"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureCard(
                icon = {
                    Icon(
                        Icons.Default.Call,
                        null,
                        tint = Color(0xFF2563EB)
                    )
                },
                title = "Emergency Calls",
                subtitle = "Call for help immediately"
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureCard(
                icon = {
                    Icon(
                        Icons.Default.People,
                        null,
                        tint = Color(0xFF7C3AED)
                    )
                },
                title = "Trusted Contacts",
                subtitle = "Alert your loved ones"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate(Routes.PERMISSION_SCREEN)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB)
                )
            ) {

                Text(
                    "Get Started",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            icon()

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun WelcomeScreenPreview() {

    MaterialTheme {

        IntroScreen(
            navController = rememberNavController()
        )
    }
}