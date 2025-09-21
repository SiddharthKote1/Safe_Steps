import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HelpScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context= LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Help & How to Use the App",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "1. **Edit Profile**:\n" +
                    "   - Go to the Edit Profile option in the menu.\n" +
                    "   - Update your personal details like name, email, etc.\n\n" +
                    "2. **Settings**:\n" +
                    "   - Go to Settings in the menu.\n" +
                    "   - Enable Accessibility permissions to allow app features to work properly.\n\n" +
                    "3. **Blocking Apps or Features**:\n" +
                    "   - Use the app to block specific apps or features for child accounts.\n" +
                    "   - Customize what you want to allow or restrict.\n\n" +
                    "4. **Time Limits**:\n" +
                    "   - Set daily or weekly time limits for apps.\n" +
                    "   - The app will notify when limits are exceeded.\n\n" +
                    "5. **Website & Keyword Blocking**:\n" +
                    "   - Block specific websites or keywords for child accounts.\n\n" +
                    "6. **Help & Support**:\n" +
                    "   - Use this Help screen for guidance.\n" +
                    "   - Contact support if you face any issues.",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate(Routes.NEE_SCREEN)},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    MaterialTheme {
        Surface {
            HelpScreen(navController = NavController(context = LocalContext.current))
        }
    }
}
