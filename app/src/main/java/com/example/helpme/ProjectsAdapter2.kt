package com.example.helpme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.ProjectDetail
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectsAdapter2(
    private val context: Context,
    private var projects: List<ProjectDetail>,
    private val onItemClick: (ProjectDetail?) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter2.ProjectViewHolder>() {

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        val datesTextView: TextView = itemView.findViewById(R.id.project_dates)
        val languageTextView: TextView = itemView.findViewById(R.id.project_language)
        val typeTextView: TextView = itemView.findViewById(R.id.project_type)
        val likesTextView: TextView = itemView.findViewById(R.id.project_likes)
        val likeImageView: ImageView = itemView.findViewById(R.id.icon_like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project2, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.titleTextView.text = project.title

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

        holder.datesTextView.text = "$startDate - $endDate"

        holder.languageTextView.text = project.lan ?: "언어 미정"
        holder.typeTextView.text = project.type ?: "타입 미정"
        holder.likesTextView.text = (project.likes ?: 0).toString() // 수정된 부분

        holder.likeImageView.setImageResource(R.drawable.ic_heart_on)

        holder.itemView.setOnClickListener {
            onItemClick(project)
        }
    }


    override fun getItemCount(): Int {
        return projects.size
    }

    fun updateProjects(newProjects: List<ProjectDetail>) {
        projects = newProjects
        notifyDataSetChanged()
    }
}
