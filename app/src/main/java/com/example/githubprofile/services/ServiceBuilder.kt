package com.example.githubprofile.services

import com.example.githubprofile.BuildConfig
import com.example.githubprofile.helpers.Config
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = BuildConfig.ACCESS_TOKEN

        val requestBuilder = original.newBuilder()
            .addHeader("User-Agent", "GithubProfile-App")
            .addHeader("Accept", "application/vnd.github.v3+json")

        if (token.isNotBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        var response = chain.proceed(request)

        // If HTTP 401 (Bad Credentials / Expired Token), retry without Authorization header for public access
        if (response.code == 401 && token.isNotBlank()) {
            response.close()
            val unauthenticatedRequest = original.newBuilder()
                .addHeader("User-Agent", "GithubProfile-App")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build()
            response = chain.proceed(unauthenticatedRequest)
        }

        response
    }

    private val loggingInterceptor = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)

    private val retrofit: Retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}
