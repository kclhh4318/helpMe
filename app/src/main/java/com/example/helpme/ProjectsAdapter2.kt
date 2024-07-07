package com.example.helpme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectsAdapter2(
    private val projects: List<Project>,
    private val onItemClick: (Project?) -> Unit
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
        holder.datesTextView.text = if (project.endDate != null) {
            "${project.startDate} - ${project.endDate}"
        } else {
            "${project.startDate} - 진행 중"
        }
        holder.languageTextView.text = project.language
        holder.typeTextView.text = project.type
        holder.likesTextView.text = project.likes.toString()

        holder.likeImageView.setImageResource(if (project.isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)

        holder.likeImageView.setOnClickListener {
            project.isLiked = !project.isLiked
            holder.likeImageView.setImageResource(if (project.isLiked) R.drawable.ic_heart_on else R.drawable.ic_heart_off)
            project.likes += if (project.isLiked) 1 else -1
            holder.likesTextView.text = project.likes.toString()
        }

        holder.itemView.setOnClickListener {
            onItemClick(project)
        }
    }

    override fun getItemCount(): Int {
        return projects.size
    }
}
