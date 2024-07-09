package com.example.helpme

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpme.model.Project
import com.example.helpme.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.helpme.network.RetrofitClient

class MyPageActivity : AppCompatActivity() {

    private lateinit var nickname: String
    private lateinit var email: String
    private lateinit var profileImage: String
    private lateinit var projects: MutableList<Project>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        nickname = intent.getStringExtra("nickname") ?: "No Nickname"
        email = intent.getStringExtra("email") ?: "No Email"
        profileImage = intent.getStringExtra("profile_image") ?: ""

        // Initialize Views
        val profileImageView = findViewById<ImageView>(R.id.profile_image)
        val nicknameTextView = findViewById<TextView>(R.id.nickname)
        val emailTextView = findViewById<TextView>(R.id.email)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val exitIcon = findViewById<ImageView>(R.id.ic_exit)

        // Set profile data
        Glide.with(this).load(profileImage).into(profileImageView)
        nicknameTextView.text = nickname
        emailTextView.text = email

        // Set RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        projects = mutableListOf()
        val adapter = MyPageProjectAdapter(projects, email)
        recyclerView.adapter = adapter

        // Load projects from server
        loadProjectsFromServer(adapter)

        // Exit functionality
        exitIcon.setOnClickListener {
            finish()
        }
    }

    private fun loadProjectsFromServer(adapter: MyPageProjectAdapter) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getUserProjects(email).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful) {
                    projects.clear()
                    response.body()?.let {
                        projects.addAll(it)
                    }
                    projects.sortBy { it.start_d }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MyPageActivity, "프로젝트를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Toast.makeText(this@MyPageActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
