package com.Siddharth.SafeSteps.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

object SupabaseConfig {
    // Developers can replace these placeholders with their actual Supabase project details.
    const val SUPABASE_URL = "https://ujovmlfyuebroqmlehbv.supabase.co"
    const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVqb3ZtbGZ5dWVicm9xbWxlaGJ2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODA0OTI3NjIsImV4cCI6MjA5NjA2ODc2Mn0.H9LilepMm8otaju7-tJ7-9fVRjlCu8XYVcsWgS0u494"
}

interface SupabaseAuthService {
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: SupabaseSignUpRequest
    ): SupabaseAuthResponse

    @POST("auth/v1/token")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Query("grant_type") grantType: String = "password",
        @Body request: SupabaseSignInRequest
    ): SupabaseAuthResponse
}

data class SupabaseSignUpRequest(
    val email: String,
    val password: String,
    val phone: String? = null,
    val data: Map<String, Any>? = null
)

data class SupabaseSignInRequest(
    val email: String? = null,
    val phone: String? = null,
    val password: String
)

data class SupabaseAuthResponse(
    val access_token: String?,
    val token_type: String?,
    val expires_in: Long?,
    val refresh_token: String?,
    val user: SupabaseUser?
)

data class SupabaseUser(
    val id: String,
    val email: String?,
    val phone: String?,
    val user_metadata: Map<String, Any>?
)

object SupabaseClient {
    val authService: SupabaseAuthService by lazy {
        val url = if (SupabaseConfig.SUPABASE_URL.endsWith("/")) {
            SupabaseConfig.SUPABASE_URL
        } else {
            "${SupabaseConfig.SUPABASE_URL}/"
        }
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseAuthService::class.java)
    }
}
