package com.example.helpme

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        projects = loadProjects()
            .filter { it.email == email }
            .sortedBy { it.startDate }
            .toMutableList()

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
        recyclerView.adapter = MyPageProjectAdapter(projects)

        // Exit functionality
        exitIcon.setOnClickListener {
            finish()
        }
    }

    private fun loadProjects(): List<Project> {
        // This method should return a list of projects
        // You can load projects from a database, API, or hardcoded data
        return listOf(
            Project("Project 1", "2023-01-01", "2023-06-01", "Kotlin", "Ongoing", "Content 1", email = email),
            Project("Project 2", "2022-01-01", "2022-06-01", "Java", "Completed", "Content 2", email = email)
        )
    }
}
