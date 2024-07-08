package com.example.helpme

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityProjectDetailBinding

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var project: Project
    private lateinit var currentUserEmail: String
    private lateinit var dbHelper: LikedProjectsDatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = LikedProjectsDatabaseHelper(this)
        db = dbHelper.writableDatabase

        currentUserEmail = intent.getStringExtra("currentUserEmail") ?: ""
        project = intent.getParcelableExtra("project")!!

        val isCurrentUserProject = project.email == currentUserEmail
        project.isLiked = isProjectLiked(project, currentUserEmail)

        setHeartIcon(project.isLiked, isCurrentUserProject)

        binding.heartIcon.setOnClickListener {
            if (!isCurrentUserProject) {
                project.isLiked = !project.isLiked
                project.likes = if (project.isLiked) project.likes + 1 else project.likes - 1
                setHeartIcon(project.isLiked, isCurrentUserProject)
                updateProjectLikes(project)
                if (project.isLiked) {
                    saveLikedProject(project, currentUserEmail)
                } else {
                    removeLikedProject(project, currentUserEmail)
                }
                setResult(RESULT_OK, Intent().putExtra("updatedProject", project))
            }
        }

        setupViewPagerAndTabs()
        binding.exitIcon.setOnClickListener {
            finish()
        }
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
        // 서버와 통신하여 프로젝트의 좋아요 상태를 업데이트하는 코드 추가
    }

    private fun setupViewPagerAndTabs() {
        val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, project)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    private fun saveLikedProject(project: Project, email: String) {
        val values = ContentValues().apply {
            put(LikedProjectsDatabaseHelper.COLUMN_PROJECT_ID, project.title)
            put(LikedProjectsDatabaseHelper.COLUMN_USER_EMAIL, email)
        }
        db.insert(LikedProjectsDatabaseHelper.TABLE_NAME, null, values)
    }

    private fun removeLikedProject(project: Project, email: String) {
        val selection = "${LikedProjectsDatabaseHelper.COLUMN_PROJECT_ID} = ? AND ${LikedProjectsDatabaseHelper.COLUMN_USER_EMAIL} = ?"
        val selectionArgs = arrayOf(project.title, email)
        db.delete(LikedProjectsDatabaseHelper.TABLE_NAME, selection, selectionArgs)
    }

    private fun isProjectLiked(project: Project, email: String): Boolean {
        val selection = "${LikedProjectsDatabaseHelper.COLUMN_PROJECT_ID} = ? AND ${LikedProjectsDatabaseHelper.COLUMN_USER_EMAIL} = ?"
        val selectionArgs = arrayOf(project.title, email)
        val cursor = db.query(
            LikedProjectsDatabaseHelper.TABLE_NAME,
            null,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val isLiked = cursor.count > 0
        cursor.close()
        return isLiked
    }
}
