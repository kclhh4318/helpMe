package com.example.helpme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpme.network.ApiService
import com.example.helpme.network.Project
import com.example.helpme.network.RetrofitClient
import kotlinx.coroutines.launch

class MyLearningFragment : Fragment() {

    private lateinit var adapter: ProjectsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_learning, container, false)

        val nickname = arguments?.getString("nickname") ?: "No Nickname"
        val email = arguments?.getString("email") ?: "No Email"
        val profileImage = arguments?.getString("profile_image") ?: ""

        // 프로필 설정
        val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        val nicknameTextView: TextView = view.findViewById(R.id.text_my_learning)
        val mostUsedTextView: TextView = view.findViewById(R.id.text_most_used)

        nicknameTextView.text = "$nickname 님, 어서오세요!"
        Glide.with(this)
            .load(profileImage)
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(profileImageView)

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectsAdapter(emptyList()) { project ->
            if (project == null) {
                showAddProjectDialog()
            } else {
                // 프로젝트 상세 페이지로 이동
            }
        }
        recyclerView.adapter = adapter

        // 진행 중인 프로젝트 가져오기
        fetchOngoingProjects(email)

        return view
    }

    private fun fetchOngoingProjects(email: String) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val projects = apiService.getOngoingProjects(email)
                adapter.updateProjects(projects)
                updateMostUsedInfo(projects)
            } catch (e: Exception) {
                Toast.makeText(context, "프로젝트 로드 실패: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateMostUsedInfo(projects: List<Project>) {
        val mostUsedLanguage = projects.groupBy { it.language }.maxByOrNull { it.value.size }?.key ?: "N/A"
        val mostUsedType = projects.groupBy { it.type }.maxByOrNull { it.value.size }?.key ?: "N/A"
        view?.findViewById<TextView>(R.id.text_most_used)?.text =
            "$mostUsedLanguage, $mostUsedType\n가장 열심히 공부하고 있어요!"
    }

    private fun showAddProjectDialog() {
        // 프로젝트 추가 다이얼로그 구현
    }
}
