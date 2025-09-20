package Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.example.SafeSteps.R
import com.example.chat.PreferencesHelper


@Composable
fun IntroScreen(navController: NavController) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.safety))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val preferencesHelper = PreferencesHelper(LocalContext.current)
    LaunchedEffect(Unit) {
        if (preferencesHelper.isAppSetupDone()) {
            navController.navigate(Routes.MAIN_SCREEN) {
                popUpTo(Routes.INTRO_SCREEN) { inclusive = true }
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize().background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF3A7BD5),
                    Color(0xFF00D2FF)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(start = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.45f)
                    .aspectRatio(1f)

            )
            // Spacer(modifier = Modifier.height(5.dp))
            Column( modifier = Modifier.offset(x = (-40).dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text("Welcome to",
                    style= MaterialTheme.typography.headlineLarge,
                    color = Color.White)
                Text("Safe Steps",
                    style = MaterialTheme.typography.titleLarge,
                    color= Color(0xFFFFC107),)
                Spacer(modifier=Modifier.height(10.dp))
                Text("Your personal safety companion design to keep you protected anytime anywhere",
                    style= MaterialTheme.typography.bodyLarge,
                    color=Color.White,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth(0.9f))
                Spacer(modifier=Modifier.height(30.dp))
                Text("Stay confident, stay connected and know that help is always" +
                        " a step away.",
                    style=MaterialTheme.typography.bodyLarge,
                    color=Color.White,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth(0.9f))
                Spacer(modifier=Modifier.height(20.dp))
                Button(onClick = {navController.navigate(Routes.PERMISSION_SCREEN)},
                   // modifier=Modifier.height(30.dp),
                colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xFFC0CF69))) {
                    Text("Get Started",
                        color=Color.DarkGray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IntroScreenPreview(){
    IntroScreen(navController = NavHostController(context = LocalContext.current))
}
