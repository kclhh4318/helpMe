package com.example.helpme

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityProjectDetailBinding
import com.example.helpme.model.ProjectDetail
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            loadProjectDetails(projectId, title, start_d, end_d, lan, type)
        } else {
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.exitIcon.setOnClickListener {
            updateProjectOnExit()
        }
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
                            isLiked = false,
                            likes = 0
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
            // Additional UI setup here
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
