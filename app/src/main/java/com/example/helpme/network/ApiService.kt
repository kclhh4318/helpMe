package com.example.helpme.network

import com.example.helpme.model.Project
import com.example.helpme.model.ProjectDetail
import com.example.helpme.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/auth/kakao/callback")
    fun saveUser(@Body user: User): Call<Void>

    @GET("/projects/user/{user_id}")
    fun getUserProjects(@Path("user_id") userId: String): Call<List<Project>>

    @POST("/projects")
    fun createProject(@Body newProject: Project): Call<Void>

    @GET("/projects/{proj_id}/details")
    fun getProjectDetails(@Path("proj_id") projectId: Int): Call<ProjectDetail>

    @PUT("/projects/{proj_id}")
    fun updateProjectDetail(@Path("proj_id") projectId: Int, @Body project: ProjectDetail): Call<Void>

    @PUT("/projects/updateContents")
    fun updateProjectContents(@Body projectDetail: ProjectDetail): Call<Void>

    @GET("/projects")
    fun getAllProjects(): Call<List<ProjectDetail>>

    @PUT("/projects/editProject/{id}")
    fun updateProject(@Path("id") id: Int, @Body project: Project): Call<Void>
}
