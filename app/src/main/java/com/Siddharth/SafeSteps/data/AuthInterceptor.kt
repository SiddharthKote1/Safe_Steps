package com.Siddharth.SafeSteps.data

import com.Siddharth.SafeSteps.PreferencesHelper
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthInterceptor : Interceptor, KoinComponent {

    private val preferencesHelper: PreferencesHelper by inject()

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val request =
            chain.request()
                .newBuilder()
                .apply {

                    preferencesHelper.getAccessToken()?.let {

                        addHeader(
                            "Authorization",
                            "Bearer $it"
                        )
                    }
                }
                .build()

        return chain.proceed(request)
    }
}