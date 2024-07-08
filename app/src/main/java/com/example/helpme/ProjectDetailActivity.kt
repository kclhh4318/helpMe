package com.example.helpme

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityProjectDetailBinding

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var project: Project
    private lateinit var currentUserEmail: String
    private lateinit var dbHelper: LikedProjectsDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LikedProjectsDatabaseHelper(this)

        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""
        project = intent.getParcelableExtra("project")!!

        val isCurrentUserProject = project.email == currentUserEmail

        // 데이터베이스에서 현재 좋아요 상태와 개수를 가져옵니다
        project.isLiked = dbHelper.isProjectLiked(project.title, currentUserEmail)
        project.likes = dbHelper.getProjectLikes(project.title)

        setHeartIcon(project.isLiked, isCurrentUserProject)

        binding.heartIcon.setOnClickListener {
            if (!isCurrentUserProject) {
                project.isLiked = !project.isLiked
                if (project.isLiked) {
                    dbHelper.saveLikedProject(project.title, currentUserEmail)
                    project.likes++
                } else {
                    dbHelper.removeLikedProject(project.title, currentUserEmail)
                    project.likes--
                }
                setHeartIcon(project.isLiked, isCurrentUserProject)
                updateProjectLikes(project)
                setResult(RESULT_OK, Intent().putExtra("updatedProject", project))
            }
        }

        setupViewPagerAndTabs()
        binding.exitIcon.setOnClickListener {
            finish()
        }

        // 프로젝트 정보 설정
        binding.projectTitle.text = project.title
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

    private fun updateProjectLikes(project: Project) {
        // 여기에 서버와 통신하여 좋아요 상태를 업데이트하는 코드를 추가할 수 있습니다.
        // 현재는 로컬 데이터베이스만 업데이트합니다.
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, project)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    // 액티비티를 종료할 때 결과를 설정합니다.
    override fun finish() {
        setResult(RESULT_OK, Intent().putExtra("updatedProject", project))
        super.finish()
    }
}
