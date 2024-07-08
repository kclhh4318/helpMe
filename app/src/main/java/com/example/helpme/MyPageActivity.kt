package com.example.helpme

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.nio.charset.Charset

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
        recyclerView.adapter = MyPageProjectAdapter(projects, email)

        // Exit functionality
        exitIcon.setOnClickListener {
            finish()
        }
    }

    private fun loadProjects(): List<Project> {
        val jsonFile = File(filesDir, "projects.json")
        return if (jsonFile.exists()) {
            val json = jsonFile.readText()
            val type = object : TypeToken<MutableList<Project>>() {}.type
            Gson().fromJson(json, type)
        } else {
            loadJSONFromAsset().toMutableList()
        }
    }

    private fun loadJSONFromAsset(): MutableList<Project> {
        val json: String?
        try {
            val inputStream = assets.open("projects.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return mutableListOf()
        }

        val projectsList = mutableListOf<Project>()
        val projectsArray = JSONArray(json)

        for (i in 0 until projectsArray.length()) {
            val project = projectsArray.getJSONObject(i)
            val title = project.getString("title")
            val startDate = project.getString("startDate")
            val endDate = project.optString("endDate", null)
            val language = project.getString("language")
            val type = project.getString("type")
            val contents = project.optString("contents", "")
            val isLiked = project.optBoolean("isLiked", false)
            val email = project.getString("email")
            projectsList.add(Project(title, startDate, endDate, language, type, contents, isLiked, email = email))
        }

        return projectsList
    }
}
