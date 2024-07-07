package com.example.helpme.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class User(
    val email: String,
    val nickname: String
)


interface ApiService {
    @POST("/auth/kakao/callback")
    fun saveUser(@Body user: User): Call<Void>
}
