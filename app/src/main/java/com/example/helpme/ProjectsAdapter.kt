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
import java.util.Locale

class ProjectsAdapter(
    private val projects: List<Project>,
    private val onItemClicked: (Project?) -> Unit,
    private val onItemLongClicked: (Project) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_ADD = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == projects.size) VIEW_TYPE_ADD else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
            ProjectViewHolder(view, onItemClicked, onItemLongClicked)
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

    inner class ProjectViewHolder(
        itemView: View,
        private val onItemClicked: (Project?) -> Unit,
        private val onItemLongClicked: (Project) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val iconFolder: ImageView = itemView.findViewById(R.id.icon_folder)
        private val titleTextView: TextView = itemView.findViewById(R.id.project_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.project_date)
        private val languageTextView: TextView = itemView.findViewById(R.id.project_language)
        private val typeTextView: TextView = itemView.findViewById(R.id.project_type)

        fun bind(project: Project) {
            titleTextView.text = project.title
            Log.d("ProjectsAdapter", "바인딩 중인 프로젝트: $project")

            Log.d("ProjectsAdapter", "Language: ${project.lan}")

            languageTextView.text = project.lan?.takeIf { it.isNotBlank() } ?: "언어 미정"
            typeTextView.text = project.type?.takeIf { it.isNotBlank() } ?: "타입 미정"

            Log.d("ProjectsAdapter", "설정된 언어: ${languageTextView.text}, 타입: ${typeTextView.text}")

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProjectDetailActivity::class.java)
                intent.putExtra("proj_id", project.proj_id)  // Ensure proj_id is passed correctly
                itemView.context.startActivity(intent)
            }

            itemView.setOnLongClickListener {
                onItemLongClicked(project)
                true
            }

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

            // 언어와 타입이 제대로 표시되도록 설정
            languageTextView.text = project.lan?.takeIf { it.isNotBlank() } ?: "언어 미정"
            typeTextView.text = project.type?.takeIf { it.isNotBlank() } ?: "타입 미정"

            // 폴더 아이콘 설정 (이미지가 이미 추가된 상태여야 합니다)
            iconFolder.setImageResource(R.drawable.ic_folder)

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, ProjectDetailActivity::class.java)
                intent.putExtra("proj_id", project.proj_id)  // Pass the proj_id
                context.startActivity(intent)
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
