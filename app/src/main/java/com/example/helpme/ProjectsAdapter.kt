package com.example.helpme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        private val iconFolder: ImageView = itemView.findViewById(R.id.icon_folder)
        private val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.project_date)
        private val languageTextView: TextView = itemView.findViewById(R.id.project_language)
        private val typeTextView: TextView = itemView.findViewById(R.id.project_type)

        fun bind(project: Project) {
            titleTextView.text = project.title

            val dateText = if (project.endDate == null) {
                "${project.startDate} ~ 진행 중"
            } else {
                "${project.startDate} ~ ${project.endDate}"
            }
            dateTextView.text = dateText

            languageTextView.text = project.language
            typeTextView.text = project.type

            // 폴더 아이콘 설정 (이미지가 이미 추가된 상태여야 합니다)
            iconFolder.setImageResource(R.drawable.ic_folder)
        }
    }
}
