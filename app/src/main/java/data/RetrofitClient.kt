package data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL =
        "https://safesteps-backend-douj.onrender.com/"

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