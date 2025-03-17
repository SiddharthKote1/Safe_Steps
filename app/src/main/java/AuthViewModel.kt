import android.app.Activity
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    var phoneNumber = mutableStateOf("")
    var otpCode = mutableStateOf("")
    var verificationId = mutableStateOf("")
    var isOtpSent = mutableStateOf(false)
    var isVerified = mutableStateOf(false)

    private lateinit var auth: FirebaseAuth
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken

    init {
        auth = FirebaseAuth.getInstance()
    }

    fun sendOtp(activity: Activity) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.value)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("AuthViewModel", "OTP Verification Failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@AuthViewModel.verificationId.value = verificationId
                    resendingToken = token
                    isOtpSent.value = true
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp() {
        val credential = PhoneAuthProvider.getCredential(verificationId.value, otpCode.value)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isVerified.value = true
                    Log.d("AuthViewModel", "OTP Verification Successful")
                } else {
                    Log.e("AuthViewModel", "OTP Verification Failed: ${task.exception?.message}")
                }
            }
    }
}
