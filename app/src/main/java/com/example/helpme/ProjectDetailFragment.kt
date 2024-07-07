package com.example.helpme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ProjectDetailFragment : Fragment() {

    private lateinit var project: Project
    private lateinit var likeButton: ImageView
    private lateinit var projectTitle: TextView
    private lateinit var exitButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_detail, container, false)

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", false)

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)

        val adapter = ProjectDetailPagerAdapter(this, project)
        viewPager.adapter = adapter

        // 드래그를 통해 페이지 전환을 비활성화
        viewPager.isUserInputEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Contents"
                1 -> "Reference"
                2 -> "Remember"
                else -> ""
            }
        }.attach()

        likeButton = view.findViewById(R.id.like_button)
        projectTitle = view.findViewById(R.id.project_title)
        exitButton = view.findViewById(R.id.exit_button)

        projectTitle.text = project.title
        likeButton.setImageResource(if (project.isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)

        likeButton.setOnClickListener {
            project.isLiked = !project.isLiked
            likeButton.setImageResource(if (project.isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)
            // 여기서 데이터베이스에 좋아요 상태를 저장하는 코드 추가 가능
        }

        exitButton.setOnClickListener {
            (activity as MainActivity).supportFragmentManager.popBackStack()
        }

        return view
    }
}
