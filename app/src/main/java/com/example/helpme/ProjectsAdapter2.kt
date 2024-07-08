package com.example.helpme

import android.content.Context
import android.content.ContentValues
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectsAdapter2(
    private val context: Context,
    private val projects: List<Project>,
    private val onItemClick: (Project?) -> Unit
) : RecyclerView.Adapter<ProjectsAdapter2.ProjectViewHolder>() {

    private val dbHelper = LikedProjectsDatabaseHelper(context)
    private val db = dbHelper.writableDatabase

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
            updateProjectLikes(project)
        }

        holder.itemView.setOnClickListener {
            onItemClick(project)
        }
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    private fun updateProjectLikes(project: Project) {
        if (project.isLiked) {
            saveLikedProject(project, project.email)
        } else {
            removeLikedProject(project, project.email)
        }
    }

    private fun saveLikedProject(project: Project, email: String) {
        val values = ContentValues().apply {
            put(LikedProjectsDatabaseHelper.COLUMN_PROJECT_ID, project.title)
            put(LikedProjectsDatabaseHelper.COLUMN_USER_EMAIL, email)
        }
        db.insert(LikedProjectsDatabaseHelper.TABLE_NAME, null, values)
    }

    private fun removeLikedProject(project: Project, email: String) {
        val selection = "${LikedProjectsDatabaseHelper.COLUMN_PROJECT_ID} = ? AND ${LikedProjectsDatabaseHelper.COLUMN_USER_EMAIL} = ?"
        val selectionArgs = arrayOf(project.title, email)
        db.delete(LikedProjectsDatabaseHelper.TABLE_NAME, selection, selectionArgs)
    }
}
