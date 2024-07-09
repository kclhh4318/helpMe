package com.example.helpme.network

import com.example.helpme.model.Project
import com.example.helpme.model.ProjectDetail
import com.example.helpme.model.User
import com.example.helpme.model.ProjectId
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/auth/kakao/callback")
    fun saveUser(@Body user: User): Call<Void>

    //유저의 프로젝트들 모두 불러오기
    @GET("/user")
    fun getUserProjects(@Query("user_id") userId: String): Call<List<Project>>

    //새 프로젝트 만들기
    @POST("/newproj")
    fun createProject(@Body newProject: Project): Call<Void>

    @POST("/idin")
    fun createProjectIn(@Body projectId: ProjectId): Call<Void>

    @POST("/detailin")
    fun createProjectDetail(@Body newProjectDetail: ProjectDetail): Call<Void>

    //프로젝트 상세 정보 가져오기
    @GET("/detailout")
    fun getProjectDetails(@Query("proj_id") projId: Int): Call<List<ProjectDetail>>

    //프로젝트 상세 정보 업데이트
    @PUT("/updateprojdetail/{proj_id}")
    fun updateProjectDetail(@Path("proj_id") projectId: Int, @Body project: ProjectDetail): Call<Void>

    //프로젝트 업데이트
    @PUT("/updateproj/{proj_id}")
    fun updateProject(@Path("proj_id") id: Int, @Body projectDetail: Project): Call<Void>

    //전달값이 없으므로 @Query를 작성할 필요가 없다.
    @GET("/orderbylikes")
    fun getAllProjects(): Call<List<ProjectDetail>>
}
