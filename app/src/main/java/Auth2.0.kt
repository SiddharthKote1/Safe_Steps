package com.example.chat

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class GoogleSignInHelper(private val activity: Activity) {

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    val client: GoogleSignInClient = GoogleSignIn.getClient(activity, gso)

    fun handleSignInResult(task: Task<GoogleSignInAccount>): GoogleSignInResult {
        return try {
            val account = task.getResult(ApiException::class.java)
            GoogleSignInResult.Success(
                email = account?.email ?: "Unknown Email",
                displayName = account?.displayName ?: "Unknown Name"
            )
        } catch (e: Exception) {
            GoogleSignInResult.Failure(e)
        }
    }
}

sealed class GoogleSignInResult {
    data class Success(val email: String, val displayName: String) : GoogleSignInResult()
    data class Failure(val error: Exception) : GoogleSignInResult()
}
