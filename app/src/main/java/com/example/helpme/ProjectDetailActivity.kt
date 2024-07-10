package com.example.helpme

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.helpme.databinding.ActivityProjectDetailBinding
import com.example.helpme.model.ProjectDetail
import com.example.helpme.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.helpme.model.ProjectId
import com.example.helpme.network.RetrofitClient
import androidx.constraintlayout.widget.ConstraintSet

class ProjectDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectDetailBinding
    private var projectDetail: ProjectDetail? = null
    private lateinit var currentUserEmail: String
    private var projectId: Int = -1
    private lateinit var currentUserId: String // 현재 사용자 ID 추가
    private var heartIcon: ImageView? = null
    private lateinit var sharedPreferences: SharedPreferences
    private var isProjectOwner: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ProjectDetailActivity", "onCreate started")
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences("ProjectPrefs", Context.MODE_PRIVATE)

        // 인텐트로부터 데이터 수신
        projectId = intent.getIntExtra("proj_id", -1)
        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""
        val projectOwnerEmail = intent.getStringExtra("projectOwnerEmail") ?: ""
        isProjectOwner = currentUserEmail.trim().toLowerCase() == projectOwnerEmail.trim().toLowerCase()

        Log.d("ProjectDetailActivity", "Received currentUserEmail: $currentUserEmail")
        Log.d("ProjectDetailActivity", "Received projectOwnerEmail: $projectOwnerEmail")

        currentUserId = intent.getStringExtra("currentUserId") ?: "" // 현재 사용자 ID 수신


        val title = intent.getStringExtra("title") ?: "Untitled Project"
        val start_d = intent.getStringExtra("start_d")
        val end_d = intent.getStringExtra("end_d")
        val lan = intent.getStringExtra("lan")
        val type = intent.getStringExtra("type")

        if (projectId != -1) {
            Log.d("ProjectDetailActivity", "Calling createProjectInDbAndLoadDetails with projectId: $projectId")
            createProjectInDbAndLoadDetails(projectId, title, start_d, end_d, lan, type)
        } else {
            Log.e("ProjectDetailActivity", "Invalid Project ID")
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.exitIcon.setOnClickListener {
            updateProjectOnExit()
            returnToExploreFragment()
        }
        Log.d("ProjectDetailActivity", "onCreate finished")

    }

    private fun createProjectInDbAndLoadDetails(projectId: Int, title: String, start_d: String?, end_d: String?, lan: String?, type: String?) {
        Log.d("ProjectDetailActivity", "createProjectInDbAndLoadDetails started")
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        val projectIdBody = ProjectId(projectId)
        apiService.createProjectIn(projectIdBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("ProjectDetailActivity", "createProjectIn onResponse: ${response.isSuccessful}")
                if (response.isSuccessful) {
                    Log.d("ProjectDetailActivity", "Project ID created in DB successfully")
                } else {
                    Log.e("ProjectDetailActivity", "Failed to create project ID in DB: ${response.errorBody()?.string()}")
                }
                Log.d("ProjectDetailActivity", "Calling loadProjectDetails")
                // 응답과 상관없이 프로젝트 세부 정보를 로드
                loadProjectDetails(projectId, title, start_d, end_d, lan, type)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ProjectDetailActivity", "Network error while creating project ID: ${t.message}")
                Log.d("ProjectDetailActivity", "Calling loadProjectDetails after failure")
                Toast.makeText(this@ProjectDetailActivity, "Network error, attempting to load details...", Toast.LENGTH_SHORT).show()
                // 네트워크 오류가 발생해도 프로젝트 세부 정보를 로드
                loadProjectDetails(projectId, title, start_d, end_d, lan, type)
            }
        })
    }

    private fun loadProjectDetails(projectId: Int, title: String, start_d: String?, end_d: String?, lan: String?, type: String?) {
        Log.d("ProjectDetailActivity", "loadProjectDetails called")
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getProjectDetails(projectId).enqueue(object : Callback<List<ProjectDetail>> {
            override fun onResponse(call: Call<List<ProjectDetail>>, response: Response<List<ProjectDetail>>) {
                Log.d("ProjectDetailActivity", "API response received")
                if (response.isSuccessful) {
                    val projectDetails = response.body()
                    Log.d("ProjectDetailActivity", "API Response: $projectDetails")
                    if (projectDetails != null && projectDetails.isNotEmpty()) {
                        projectDetail = projectDetails[0].copy(proj_id = projectId)
                        Log.d("ProjectDetailActivity", "Project detail loaded: $projectDetail")
                    } else {
                        Log.e("ProjectDetailActivity", "No project details found for proj_id: $projectId")
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
                            isLiked = getLikeStatusFromPreferences(projectId),
                            likes = 0
                        )
                        Log.d("ProjectDetailActivity", "Created new project detail: $projectDetail")
                    }
                    Log.d("ProjectDetailActivity", "Calling setupUI")
                    setupUI()
                } else {
                    Log.e("ProjectDetailActivity", "Response unsuccessful: ${response.errorBody()?.string()}")
                    Log.d("ProjectDetailActivity", "Calling setupUI after unsuccessful response")
                    Toast.makeText(this@ProjectDetailActivity, "Failed to load project details", Toast.LENGTH_SHORT).show()
                    setupUI() // API 호출 실패 시에도 setupUI 호출
                }
            }

            override fun onFailure(call: Call<List<ProjectDetail>>, t: Throwable) {
                Log.e("ProjectDetailActivity", "Network error: ${t.message}")
                Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("ProjectDetailActivity", "Calling setupUI after network error")
                setupUI() // 네트워크 오류 시에도 setupUI 호출
            }
        })
    }

    private fun setupUI() {
        Log.d("ProjectDetailActivity", "setupUI started")
        projectDetail?.let {
            Log.d("ProjectDetailActivity", "Setting up UI with project: ${it.title}")
            binding.projectTitle.text = it.title

            if (currentUserEmail.trim().toLowerCase() != it.email?.trim()?.toLowerCase()) {

                Log.d("ProjectDetailActivity", "Adding heart icon")

                addHeartIcon()

                updateLikeIcon(it.isLiked == true)
            }
            Log.d("ProjectDetailActivity", "Setting up ViewPager and Tabs")

            setupViewPagerAndTabs()
        }?: Log.e("ProjectDetailActivity", "projectDetail is null in setupUI")
    }

    private fun setupViewPagerAndTabs() {
        projectDetail?.let {
            val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, it, isProjectOwner)
            binding.viewPager.adapter = pagerAdapter
            binding.tabLayout.setupWithViewPager(binding.viewPager)
        }
    }

    private fun addHeartIcon() {
        heartIcon = ImageView(this).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                width = resources.getDimensionPixelSize(R.dimen.heart_icon_size)
                height = resources.getDimensionPixelSize(R.dimen.heart_icon_size)
                marginStart = resources.getDimensionPixelSize(R.dimen.heart_icon_margin)
                topMargin = resources.getDimensionPixelSize(R.dimen.heart_icon_margin)
            }
            setImageResource(R.drawable.ic_heart_off)
            setOnClickListener { toggleLikeStatus() }
        }

        (binding.root as ConstraintLayout).addView(heartIcon)

        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.root as ConstraintLayout)
        constraintSet.connect(heartIcon!!.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
        constraintSet.connect(heartIcon!!.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
        constraintSet.applyTo(binding.root as ConstraintLayout)
    }

    private fun updateLikeIcon(isLiked: Boolean) {
        heartIcon?.setImageResource(if (isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)
    }

    private fun toggleLikeStatus() {
        projectDetail?.let {
            it.isLiked = !(it.isLiked ?: false)
            updateLikeIcon(it.isLiked == true)
            saveLikeStatusToPreferences(it.proj_id, it.isLiked == true) // 로컬 저장소에 좋아요 상태 저장
        }
    }

    private fun saveLikeStatusToPreferences(projectId: Int, isLiked: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("isLiked_$projectId", isLiked)
            apply()
        }
    }

    private fun getLikeStatusFromPreferences(projectId: Int): Boolean {
        return sharedPreferences.getBoolean("isLiked_$projectId", false)
    }

    private fun updateProjectOnExit() {
        projectDetail?.let {
            Log.d("ProjectDetailActivity", "Updating Project: ID=${it.proj_id}, Title=${it.title}")
            Log.d("ProjectDetailActivity", "Contents: ${it.contents}")
            Log.d("ProjectDetailActivity", "Remember: ${it.remember}")
            Log.d("ProjectDetailActivity", "Reference before API call: ${it.ref}")
            Log.d("ProjectDetailActivity", "Likes before API call: ${it.isLiked}")

            projectDetail?.let {
                it.proj_id = projectId
            } ?: finish()

            val apiService = RetrofitClient.instance.create(ApiService::class.java)
            apiService.updateProjectDetail(it).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    Log.d("ProjectDetailActivity", "Response Code: ${response.code()}")
                    Log.d("ProjectDetailActivity", "Response Body: ${response.body()}")
                    Log.d("ProjectDetailActivity", "Error Body: ${response.errorBody()?.string()}")
                    Log.d("ProjectDetailActivity", "Reference after API call: ${it.ref}")
                    Log.d("ProjectDetailActivity", "Likes after API call: ${it.isLiked}")
                    if (!response.isSuccessful) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProjectDetailActivity", "Failed to update project details: $errorBody")
                        Toast.makeText(this@ProjectDetailActivity, "Failed to update project details", Toast.LENGTH_SHORT).show()
                    }
                    broadcastUpdate()
                    finish()
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProjectDetailActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                    finish()
                }
            })
        } ?: finish()
    }

    private fun broadcastUpdate() {
        val intent = Intent("com.example.helpme.PROJECT_UPDATED")
        sendBroadcast(intent)
    }

    private fun returnToExploreFragment() {
        val intent = Intent()
        intent.putExtra("refresh", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    fun updateProjectDetail(updatedProject: ProjectDetail) {
        projectDetail?.let {
            it.proj_id = updatedProject.proj_id
            it.contents = updatedProject.contents
            it.remember = updatedProject.remember
            it.ref = updatedProject.ref
            Log.d("ProjectDetailActivity", "Updated project detail: ${it.ref}")
        }
    }

    override fun onBackPressed() {
        updateProjectOnExit()
        returnToExploreFragment()
        super.onBackPressed()
    }
}
