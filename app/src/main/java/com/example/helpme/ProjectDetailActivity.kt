package com.example.helpme

import android.os.Bundle
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
    private lateinit var projectDetail: ProjectDetail
    private lateinit var currentUserEmail: String
    private var projectId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""
        projectId = intent.getIntExtra("proj_id", -1)

        if (projectId != -1) {
            loadProjectDetails(projectId)
        } else {
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.exitIcon.setOnClickListener {
            updateProjectOnExit()
        }
    }

    private fun loadProjectDetails(projectId: Int) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getProjectDetails(projectId).enqueue(object : Callback<ProjectDetail> {
            override fun onResponse(call: Call<ProjectDetail>, response: Response<ProjectDetail>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        projectDetail = it
                        if (projectDetail.start_d == null) {
                            Toast.makeText(this@ProjectDetailActivity, "Project start date is missing", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            setupUI()
                        }
                    } ?: run {
                        Toast.makeText(this@ProjectDetailActivity, "No project details found", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Toast.makeText(this@ProjectDetailActivity, "Failed to load project details", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<ProjectDetail>, t: Throwable) {
                Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun setupUI() {
        binding.projectTitle.text = projectDetail.title
        setupViewPagerAndTabs()
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, projectDetail)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun updateProjectOnExit() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.updateProjectDetail(projectDetail.proj_id, projectDetail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    Toast.makeText(this@ProjectDetailActivity, "Failed to update project details", Toast.LENGTH_SHORT).show()
                }
                finish()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    override fun onBackPressed() {
        updateProjectOnExit()
        super.onBackPressed()
    }
}
