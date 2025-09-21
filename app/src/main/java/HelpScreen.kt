import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp,top=50.dp,end=24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Help & How to Use SafeSteps",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFFFFF176),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text(
                text = """
                    1. Permissions  
                    - Allow Phone and SMS permissions so the app can place a call and send a text in an emergency.  
                    - Allow Location (Coarse and Fine) so your GPS position can be shared.  
                    - Turn on Accessibility Service so the app can detect the emergency button press.  

                    2. Set Emergency Contacts  
                    - Open the Emergency Contacts section in the app.  
                    - Add at least one mobile number. Two contacts are recommended.  
                    - Make sure these contacts can receive calls and texts.  

                    3. Sending an Emergency Alert  
                    - If you are in danger or need help, press and hold the Volume Up and Volume Down buttons together for about five seconds.  
                    - The app will automatically call your first saved contact and send a text with your current location to both contacts.  

                    4. Location Updates  
                    - SafeSteps updates your location periodically in the background.  
                    - Keep your phone’s location services turned on for accurate updates.  

                    5. Tips for Reliable Use  
                    - Keep Accessibility Service enabled at all times.  
                    - Turn off battery optimization for SafeSteps so it isn’t stopped in the background.  
                    - Test the alert feature with a trusted contact to confirm everything works.
                """.trimIndent(),
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))
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

