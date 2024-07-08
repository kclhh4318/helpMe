package com.example.helpme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.ProjectDetail

class MyPageProjectAdapter(private val projectList: List<ProjectDetail>, private val currentUserEmail: String) : RecyclerView.Adapter<MyPageProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectList[position]
        holder.titleTextView.text = project.title
        holder.dateTextView.text = if (project.end_d != null) {
            "${project.start_d} - ${project.end_d}"
        } else {
            "${project.start_d} - 진행 중"
        }
        holder.languageTextView.text = project.lang
        holder.typeTextView.text = project.type

        // Set click listener
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProjectDetailActivity::class.java).apply {
                putExtra("project", project)
                putExtra("currentUserEmail", currentUserEmail)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    class ProjectViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.project_title)
        val dateTextView: TextView = view.findViewById(R.id.project_date)
        val languageTextView: TextView = view.findViewById(R.id.project_language)
        val typeTextView: TextView = view.findViewById(R.id.project_type)
        val iconImageView: ImageView = view.findViewById(R.id.icon_folder)
    }
}
