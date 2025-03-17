import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.OTPScreen
import com.example.chat.PhoneScreen

@Composable
fun NavGraph(modifier:Modifier=Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "PhoneScreen") {
        composable("PhoneScreen") {
            PhoneScreen()
        }
        composable("OTPScreen"){
            OTPScreen()
        }
    }
}
