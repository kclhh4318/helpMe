package com.example.helpme.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://45e0-2001-2d8-68a5-e548-bc2b-a456-9c49-9ed7.ngrok-free.app/auth/kakao/callback" // Replace with your server URL

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
