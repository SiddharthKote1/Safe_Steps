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
    var errorMessage = mutableStateOf("")

    private lateinit var auth: FirebaseAuth
    private var resendingToken: PhoneAuthProvider.ForceResendingToken? = null

    init {
        auth = FirebaseAuth.getInstance()
    }

    // Function to send OTP
    fun sendOtp(activity: Activity) {
        if (phoneNumber.value.isBlank() || phoneNumber.value.length < 10) {
            errorMessage.value = "Enter a valid phone number"
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.value)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    errorMessage.value = "OTP Verification Failed: ${e.message}"
                    Log.e("AuthViewModel", "OTP Verification Failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@AuthViewModel.verificationId.value = verificationId
                    resendingToken = token
                    isOtpSent.value = true
                    errorMessage.value = ""
                    Log.d("AuthViewModel", "OTP Sent Successfully")
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Function to verify OTP
    fun verifyOtp() {
        if (otpCode.value.length != 6) {
            errorMessage.value = "Enter a 6-digit OTP"
            return
        }

        val credential = PhoneAuthProvider.getCredential(verificationId.value, otpCode.value)
        signInWithPhoneAuthCredential(credential)
    }

    // Function to sign in using phone credentials
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isVerified.value = true
                    errorMessage.value = ""
                    Log.d("AuthViewModel", "OTP Verification Successful")
                } else {
                    errorMessage.value = "OTP Verification Failed: ${task.exception?.message}"
                    Log.e("AuthViewModel", "OTP Verification Failed: ${task.exception?.message}")
                }
            }
    }

    // Function to resend OTP
    fun resendOtp(activity: Activity) {
        if (resendingToken == null) {
            errorMessage.value = "Please request OTP first"
            return
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber.value)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    errorMessage.value = "Resending OTP Failed: ${e.message}"
                    Log.e("AuthViewModel", "Resending OTP Failed: ${e.message}")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    this@AuthViewModel.verificationId.value = verificationId
                    resendingToken = token
                    isOtpSent.value = true
                    errorMessage.value = ""
                    Log.d("AuthViewModel", "OTP Resent Successfully")
                }
            })
            .setForceResendingToken(resendingToken!!) // Use existing resending token
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}

