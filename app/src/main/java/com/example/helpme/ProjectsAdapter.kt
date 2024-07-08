package com.example.helpme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.Project

class ProjectsAdapter(private val projects: List<Project>, private val onItemClicked: (Project?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_ADD = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == projects.size) VIEW_TYPE_ADD else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
            ProjectViewHolder(view, onItemClicked)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_add_project, parent, false)
            AddProjectViewHolder(view, onItemClicked)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProjectViewHolder && position < projects.size) {
            holder.bind(projects[position])
        }
    }

    override fun getItemCount(): Int {
        return projects.size + 1
    }

    class ProjectViewHolder(itemView: View, private val onItemClicked: (Project?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val iconFolder: ImageView = itemView.findViewById(R.id.icon_folder)
        private val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.project_date)
        private val languageTextView: TextView = itemView.findViewById(R.id.project_language)
        private val typeTextView: TextView = itemView.findViewById(R.id.project_type)

        fun bind(project: Project) {
            titleTextView.text = project.title

            val dateText = if (project.end_d == null) {
                "${project.start_d} ~ 진행 중"
            } else {
                "${project.start_d} ~ ${project.end_d}"
            }
            dateTextView.text = dateText

            languageTextView.text = project.lang ?: "언어 미정"
            typeTextView.text = project.type

            // 폴더 아이콘 설정 (이미지가 이미 추가된 상태여야 합니다)
            iconFolder.setImageResource(R.drawable.ic_folder)

            itemView.setOnClickListener {
                onItemClicked(project)
            }
        }
    }

    class AddProjectViewHolder(itemView: View, private val onItemClicked: (Project?) -> Unit) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClicked(null)
            }
        }
    }
}