package com.example.helpme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.helpme.databinding.ActivityProjectDetailBinding

class ProjectDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Project 데이터를 받아옴
        project = intent.getParcelableExtra("project")!!

        // 상단 뷰 설정
        binding.projectTitle.text = project.title
        setHeartIcon(project.isLiked)

        // 좋아요 버튼 클릭 리스너 설정
        binding.heartIcon.setOnClickListener {
            project.isLiked = !project.isLiked
            setHeartIcon(project.isLiked)
        }

        // 종료 버튼 클릭 리스너 설정
        binding.exitIcon.setOnClickListener {
            finish()
        }

        // ViewPager와 TabLayout 설정
        val pagerAdapter = ProjectDetailPagerAdapter(supportFragmentManager, project)
        binding.viewPager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        // 드래그로 탭 전환 불가 설정
        binding.viewPager.setOnTouchListener { _, _ -> true }
    }

    private fun setHeartIcon(isLiked: Boolean) {
        val iconRes = if (isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off
        binding.heartIcon.setImageResource(iconRes)
    }
}
