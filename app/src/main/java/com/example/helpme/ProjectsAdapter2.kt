package com.example.helpme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.ProjectDetail

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
        holder.datesTextView.text = if (project.end_d != null) {
            "${project.start_d} - ${project.end_d}"
        } else {
            "${project.start_d} - 진행 중"
        }
        holder.languageTextView.text = project.lan
        holder.typeTextView.text = project.type
        holder.likesTextView.text = project.likes.toString()

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
