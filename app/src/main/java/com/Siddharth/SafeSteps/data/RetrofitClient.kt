package com.Siddharth.SafeSteps.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL =
        // For local testing with physical device via adb reverse tcp:8000 tcp:8000
        "http://localhost:8000/"

    private val okHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor()
            )
            .build()

    val apiService: ApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(ApiService::class.java)
    }
}