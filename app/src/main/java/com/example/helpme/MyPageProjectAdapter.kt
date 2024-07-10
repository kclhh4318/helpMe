package com.example.helpme

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.Project
import java.text.SimpleDateFormat
import java.util.*

class MyPageProjectAdapter(
    private val projects: List<Project>,
    private val currentUserEmail: String,
    private val onItemClicked: (Project) -> Unit
) : RecyclerView.Adapter<MyPageProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
    }

    override fun getItemCount(): Int = projects.size

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconFolder: ImageView = itemView.findViewById(R.id.icon_folder)
        private val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.project_date)
        private val languageTextView: TextView = itemView.findViewById(R.id.project_language)
        private val typeTextView: TextView = itemView.findViewById(R.id.project_type)

        fun bind(project: Project) {
            titleTextView.text = project.title
            Log.d("MyPageProjectAdapter", "바인딩 중인 프로젝트: $project")

            languageTextView.text = project.lan?.takeIf { it.isNotBlank() } ?: "언어 미정"
            typeTextView.text = project.type.takeIf { it.isNotBlank() } ?: "타입 미정"

            Log.d("MyPageProjectAdapter", "설정된 언어: ${languageTextView.text}, 타입: ${typeTextView.text}")

            // 날짜 형식을 YYYY-MM-DD로 변환하여 표시
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = project.start_d?.let {
                try {
                    val date = inputFormat.parse(it)
                    outputFormat.format(date)
                } catch (e: Exception) {
                    it // 파싱 실패 시 원본 문자열 반환
                }
            } ?: "날짜 미정"
            val endDate = project.end_d?.let {
                try {
                    val date = inputFormat.parse(it)
                    outputFormat.format(date)
                } catch (e: Exception) {
                    it // 파싱 실패 시 원본 문자열 반환
                }
            } ?: "진행 중"

            dateTextView.text = "$startDate ~ $endDate"

            // 폴더 아이콘 설정
            iconFolder.setImageResource(R.drawable.ic_folder)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ProjectDetailActivity::class.java)
                intent.putExtra("proj_id", project.proj_id)
                intent.putExtra("currentUserEmail", currentUserEmail)
                intent.putExtra("projectOwnerEmail", project.email)
                intent.putExtra("title", project.title)
                intent.putExtra("start_d", project.start_d)
                intent.putExtra("end_d", project.end_d)
                intent.putExtra("lan", project.lan)
                intent.putExtra("type", project.type)
                Log.d("MyPageProjectAdapter", "Sending project details: ${project.toString()}")
                Log.d("MyPageProjectAdapter", "Sending currentUserEmail: $currentUserEmail")
                Log.d("MyPageProjectAdapter", "Sending projectOwnerEmail: ${project.email}")
                context.startActivity(intent)
            }
        }
    }
}