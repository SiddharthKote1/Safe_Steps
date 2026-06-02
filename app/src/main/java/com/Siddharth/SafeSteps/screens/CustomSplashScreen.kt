package com.Siddharth.SafeSteps.screens

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Text
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.Siddharth.SafeSteps.PreferencesHelper
import com.Siddharth.SafeSteps.R
import kotlinx.coroutines.delay

@Composable
fun CustomSplashScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val preferencesHelper = PreferencesHelper(context)

    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    
    val infiniteTransition = rememberInfiniteTransition()
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2500)
        
        val user = preferencesHelper.getUserData()
        val isUserDataComplete = user != null && user.name.isNotBlank() && user.phone1.isNotBlank()

        val nextRoute = when {
            !preferencesHelper.isOnboardingSeen() -> Routes.INTRO_SCREEN
            preferencesHelper.getAccessToken().isNullOrBlank() -> Routes.LOGIN_SCREEN
            !preferencesHelper.isAppSetupDone() -> Routes.PERMISSION_SCREEN
            !isUserDataComplete -> Routes.SETUP_CONTACTS
            else -> {
                "NeeScreen/${Uri.encode(user!!.name)}/${Uri.encode(user.countryCode1)}/${Uri.encode(user.countryCode2)}/${Uri.encode(user.phone1)}/${Uri.encode(user.phone2)}"
            }
        }

        navController.navigate(nextRoute) {
            popUpTo(Routes.SPLASH_SCREEN) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(if (startAnimation) scaleAnim else 1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "SafeSteps Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Safe Steps",
                color = androidx.compose.ui.graphics.Color.White,
                style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
            )
        }
    }
}
