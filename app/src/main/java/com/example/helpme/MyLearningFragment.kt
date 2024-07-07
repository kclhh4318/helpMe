package com.example.helpme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class MyLearningFragment : Fragment() {

    private lateinit var nickname: String
    private lateinit var email: String
    private lateinit var profileImage: String
    private lateinit var projects: List<Project>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_learning, container, false)

        val activity = activity as MainActivity
        nickname = activity.intent.getStringExtra("nickname") ?: "No Nickname"
        email = activity.intent.getStringExtra("email") ?: "No Email"
        profileImage = activity.intent.getStringExtra("profile_image") ?: ""

        // JSON 데이터 로드
        projects = loadJSONFromAsset()

        // 프로필 설정
        val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        val nicknameTextView: TextView = view.findViewById(R.id.text_my_learning)
        val emailTextView: TextView = view.findViewById(R.id.text_email)

        nicknameTextView.text = "Nickname: $nickname"
        emailTextView.text = "Email: $email"
        Glide.with(this)
            .load(profileImage)
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(profileImageView)

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ProjectsAdapter(projects)

        return view
    }

    private fun loadJSONFromAsset(): List<Project> {
        val json: String?
        try {
            val inputStream = context?.assets?.open("projects.json")
            val size = inputStream?.available()
            val buffer = ByteArray(size!!)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return emptyList()
        }

        val projectsList = mutableListOf<Project>()
        val jsonObject = JSONObject(json)
        val projectsArray = jsonObject.getJSONArray("projects")
        for (i in 0 until projectsArray.length()) {
            val project = projectsArray.getJSONObject(i)
            val title = project.getString("title")
            val startDate = project.getString("startDate")
            val endDate = project.optString("endDate")
            val language = project.getString("language")
            val type = project.getString("type")
            projectsList.add(Project(title, startDate, endDate, language, type))
        }

        return projectsList
    }
}

data class Project(val title: String, val startDate: String, val endDate: String?, val language: String, val type: String)

class ProjectsAdapter(private val projects: List<Project>) : RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount() = projects.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        private val detailsTextView: TextView = itemView.findViewById(R.id.project_details)

        fun bind(project: Project) {
            titleTextView.text = project.title
            detailsTextView.text = "Started: ${project.startDate} - Language: ${project.language} - Type: ${project.type}"

            if (project.endDate == null) {
                detailsTextView.text = "${detailsTextView.text} - Status: In Progress"
            } else {
                detailsTextView.text = "${detailsTextView.text} - Ended: ${project.endDate}"
            }
        }
    }
}
