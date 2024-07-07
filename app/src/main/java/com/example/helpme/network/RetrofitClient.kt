package com.example.helpme.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://9c06-2001-2d8-6bac-227b-c433-9b9b-b36d-c732.ngrok-free.app"  // 서버 URL로 대체

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
