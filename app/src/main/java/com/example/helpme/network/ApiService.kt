package com.example.helpme.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class User(
    val id: String,
    val email: String,
    val nickname: String
)

interface ApiService {
    @POST("/auth/kakao/callback")
    suspend fun saveUser(@Body user: User): User

    @GET("/projects")
    suspend fun getOngoingProjects(@Query("userId") userId: String): List<Project>
}
