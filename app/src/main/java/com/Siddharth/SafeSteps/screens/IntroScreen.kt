package com.Siddharth.SafeSteps.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color
)

val onboardingPages = listOf(
    OnboardingPage(
        title = "One-Tap SOS",
        description = "Alert loved ones and authorities instantly.",
        icon = Icons.Default.Notifications,
        iconColor = Color(0xFFE11D48)
    ),
    OnboardingPage(
        title = "AI Assistant",
        description = "Real-time threat assessment and safety advice.",
        icon = Icons.Default.Security,
        iconColor = Color(0xFF6D28D9)
    ),
    OnboardingPage(
        title = "Live Tracking",
        description = "Share your location continuously until you're safe.",
        icon = Icons.Default.LocationOn,
        iconColor = Color(0xFF10B981)
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferencesHelper = com.Siddharth.SafeSteps.PreferencesHelper(context)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dynamic background based on current page
            val bgImageRes = when (pagerState.currentPage) {
                0 -> com.Siddharth.SafeSteps.R.drawable.intro_bg_1
                1 -> com.Siddharth.SafeSteps.R.drawable.intro_bg_2
                else -> com.Siddharth.SafeSteps.R.drawable.intro_bg_3
            }
            
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = bgImageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Add a subtle gradient overlay so text remains readable
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(60.dp))
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { position ->
                OnboardingPageUI(page = onboardingPages[position])
            }
            
            // Pager Indicator
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                    val width = if (pagerState.currentPage == iteration) 24.dp else 12.dp
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(color)
                            .height(8.dp)
                            .width(width)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        preferencesHelper.setOnboardingSeen(true)
                        navController.navigate(Routes.LOGIN_SCREEN) {
                            popUpTo(Routes.INTRO_SCREEN) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingPages.size - 1) "Get Started" else "Next",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        } // Close Box
    }
}

@Composable
fun OnboardingPageUI(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(page.iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = page.iconColor
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}