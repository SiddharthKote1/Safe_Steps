import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.Credential
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chat.OTPScreen
import com.example.chat.PhoneScreen
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "PhoneScreen") {
        composable("PhoneScreen") {
            PhoneScreen(navController)
        }
        composable("OTP") {
            OTPScreen(
                onVerifyClick = {
                    navController.popBackStack()
                },
                navController = navController
            )
        }
    }
}

val auth=FirebaseAuth.getInstance()
var storedVerificationId=""

fun signInWithPhoneAuthCredential(context:Context,credential: PhoneAuthCredential,navController: NavController){
    auth.signInWithCredential(credential)
        .addOnCompleteListener(context as Activity) { task->
            if(task.isSuccessful){
                Toast.makeText(context,"Login Sucessfull", Toast.LENGTH_SHORT).show()
                navController.navigate("success")
                val user =task.result?.user
            }
            else{
                if(task.exception is FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(context,"Wrong OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }

    fun onLoginclicked(context:Context,navController: NavController,phoneNumber:String,onCodeSend: () -> Unit){
        auth.setLanguageCode("en")
        val callback=object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.d("phonebook","verification failed ")
                signInWithPhoneAuthCredential(context,p0,navController)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.d("phonebook","verification failed$p0")
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                Log.d("phonebook","code send$p0")
                storedVerificationId=p0
                onCodeSend()
            }

        }
        val option= PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$phoneNumber")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(option)
    }

    fun verifyPhoneNumberWithCo(context: Context,verificationId:String,code:String,navController: NavController){
        val p0 = PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(context,p0,navController)
    }
}
