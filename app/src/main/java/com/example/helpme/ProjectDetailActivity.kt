package com.example.helpme

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityProjectDetailBinding
import com.example.helpme.model.ProjectDetail
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.helpme.model.ProjectId

class ProjectDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectDetailBinding
    private var projectDetail: ProjectDetail? = null
    private lateinit var currentUserEmail: String
    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트로부터 데이터 수신
        projectId = intent.getIntExtra("proj_id", -1)
        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""

        val title = intent.getStringExtra("title") ?: "Untitled Project"
        val start_d = intent.getStringExtra("start_d")
        val end_d = intent.getStringExtra("end_d")
        val lan = intent.getStringExtra("lan")
        val type = intent.getStringExtra("type")

        if (projectId != -1) {
            createProjectInDbAndLoadDetails(projectId, title, start_d, end_d, lan, type)
        } else {
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.exitIcon.setOnClickListener {
            updateProjectOnExit()
        }

        binding.heartIcon.setOnClickListener {
            toggleLikeStatus()
        }
    }

    private fun createProjectInDbAndLoadDetails(projectId: Int, title: String, start_d: String?, end_d: String?, lan: String?, type: String?) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val projectIdBody = ProjectId(projectId)
        apiService.createProjectIn(projectIdBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("ProjectDetailActivity", "Project ID created in DB successfully")
                } else {
                    Log.e("ProjectDetailActivity", "Failed to create project ID in DB: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProjectDetailActivity, "Project may already exist, loading details...", Toast.LENGTH_SHORT).show()
                }
                // Proceed to load project details regardless of the response
                loadProjectDetails(projectId, title, start_d, end_d, lan, type)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ProjectDetailActivity", "Network error while creating project ID: ${t.message}")
                Toast.makeText(this@ProjectDetailActivity, "Network error, attempting to load details...", Toast.LENGTH_SHORT).show()
                // Proceed to load project details even if there's a network error
                loadProjectDetails(projectId, title, start_d, end_d, lan, type)
            }
        })
    }

    private fun loadProjectDetails(projectId: Int, title: String, start_d: String?, end_d: String?, lan: String?, type: String?) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getProjectDetails(projectId).enqueue(object : Callback<List<ProjectDetail>> {
            override fun onResponse(call: Call<List<ProjectDetail>>, response: Response<List<ProjectDetail>>) {
                if (response.isSuccessful) {
                    val projectDetails = response.body()
                    Log.d("ProjectDetailActivity", "API Response: $projectDetails")
                    if (projectDetails != null && projectDetails.isNotEmpty()) {
                        projectDetail = projectDetails[0]
                    } else {
                        Log.e("ProjectDetailActivity", "No project details found for proj_id: $projectId")
                        // Initialize ProjectDetail using data from the Intent
                        projectDetail = ProjectDetail(
                            proj_id = projectId,
                            title = title,
                            start_d = start_d,
                            end_d = end_d,
                            lan = lan,
                            type = type,
                            email = currentUserEmail,
                            contents = null,
                            remember = null,
                            ref = null,
                            likes = false
                        )
                    }
                    setupUI()
                } else {
                    Log.e("ProjectDetailActivity", "Response unsuccessful: ${response.errorBody()?.string()}")
                    Toast.makeText(this@ProjectDetailActivity, "Failed to load project details", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<List<ProjectDetail>>, t: Throwable) {
                Log.e("ProjectDetailActivity", "Network error: ${t.message}")
                Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun setupUI() {
        projectDetail?.let {
            binding.projectTitle.text = it.title
            // 프로젝트 이메일과 현재 사용자 이메일을 비교하여 하트 아이콘 가시성 설정
            if (it.email == currentUserEmail) {
                binding.heartIcon.visibility = View.GONE
            } else {
                binding.heartIcon.visibility = View.VISIBLE
                updateLikeIcon(it.likes == true)
            }
            setupViewPagerAndTabs()
        }
    }

    private fun setupViewPagerAndTabs() {
        projectDetail?.let {
            val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, it)
            binding.viewPager.adapter = pagerAdapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        binding.heartIcon.setImageResource(if (isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)
    }

    private fun toggleLikeStatus() {
        projectDetail?.let {
            it.likes = !(it.likes ?: false)
            Log.d("ProjectDetailActivity", "Likes status changed: ${it.likes}") // 로그 추가
            updateLikeIcon(it.likes == true)

            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            apiService.updateProjectDetail(it.proj_id, it).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("ProjectDetailActivity", "Successfully updated like status")
                    } else {
                        Log.e("ProjectDetailActivity", "Failed to update like status: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("ProjectDetailActivity", "Network error: ${t.message}")
                }
            })
        }
    }

    private fun updateProjectOnExit() {
        projectDetail?.let {
            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            apiService.updateProjectDetail(it.proj_id, it).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ProjectDetailActivity", "Response Code: ${response.code()}")
                    Log.d("ProjectDetailActivity", "Response Body: ${response.body()}")
                    Log.d("ProjectDetailActivity", "Error Body: ${response.errorBody()?.string()}")
                    if (!response.isSuccessful) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProjectDetailActivity", "Failed to update project details: $errorBody")
                        Toast.makeText(this@ProjectDetailActivity, "Failed to update project details", Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        } ?: finish()
    }

    override fun onBackPressed() {
        updateProjectOnExit()
        super.onBackPressed()
    }
}
