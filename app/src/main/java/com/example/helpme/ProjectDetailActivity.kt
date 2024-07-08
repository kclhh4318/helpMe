package com.example.helpme

import android.content.Intent
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
import java.io.Serializable

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var projectDetail: ProjectDetail
    private lateinit var currentUserEmail: String
    private lateinit var dbHelper: LikedProjectsDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LikedProjectsDatabaseHelper(this)

        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""
        val projectId = intent.getIntExtra("proj_id", -1)

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
                        setupUI()
                    } ?: run {
                        Toast.makeText(this@ProjectDetailActivity, "Project details not found", Toast.LENGTH_SHORT).show()
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
        val isCurrentUserProject = projectDetail.email == currentUserEmail

        // 데이터베이스에서 현재 좋아요 상태와 개수를 가져옵니다
        projectDetail.isLiked = dbHelper.isProjectLiked(projectDetail.title, currentUserEmail)
        projectDetail.likes = dbHelper.getProjectLikes(projectDetail.title)

        setHeartIcon(projectDetail.isLiked, isCurrentUserProject)

        binding.heartIcon.setOnClickListener {
            if (!isCurrentUserProject) {
                projectDetail.isLiked = !projectDetail.isLiked
                if (projectDetail.isLiked) {
                    dbHelper.saveLikedProject(projectDetail.title, currentUserEmail)
                    projectDetail.likes++
                } else {
                    dbHelper.removeLikedProject(projectDetail.title, currentUserEmail)
                    projectDetail.likes--
                }
                setHeartIcon(projectDetail.isLiked, isCurrentUserProject)
                updateProjectLikes(projectDetail)
                val resultIntent = Intent().putExtra("updatedProject", projectDetail as Serializable)
                setResult(RESULT_OK, resultIntent)
            }
        }

        setupViewPagerAndTabs()

        // 프로젝트 정보 설정
        binding.projectTitle.text = projectDetail.title
    }

    private fun setHeartIcon(isLiked: Boolean, isCurrentUserProject: Boolean) {
        if (isCurrentUserProject) {
            binding.heartIcon.setImageResource(R.drawable.ic_heart_off)
            binding.heartIcon.isEnabled = false
        } else {
            val iconRes = if (isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off
            binding.heartIcon.setImageResource(iconRes)
            binding.heartIcon.isEnabled = true
        }
    }

    private fun updateProjectLikes(project: ProjectDetail) {
        // 여기에 서버와 통신하여 좋아요 상태를 업데이트하는 코드를 추가할 수 있습니다.
        // 현재는 로컬 데이터베이스만 업데이트합니다.
    }

    private fun updateProjectOnExit() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.updateProjectDetail(projectDetail.proj_id, projectDetail).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    // 오류 처리
                }
                finish()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 오류 처리
                finish()
            }
        })
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, projectDetail)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    // 액티비티를 종료할 때 결과를 설정합니다.
    override fun finish() {
        updateProjectOnExit()
        super.finish()
    }
}
