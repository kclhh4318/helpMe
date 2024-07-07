package com.example.helpme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExploreFragment : Fragment() {

    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectsAdapter2

    private val languages = arrayOf("Python", "Java", "Kotlin", "C++", "JavaScript")
    private val types = arrayOf("Machine Learning", "Web Development", "Mobile Development", "Blockchain", "Game Development")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        // 프로젝트 데이터 로드
        projects = loadProjects().toMutableList()

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectsAdapter2(projects) { project ->
            // 프로젝트 아이템 클릭 이벤트 처리
        }
        recyclerView.adapter = adapter

        // 필터 버튼 설정
        setupFilterButtons(view)

        return view
    }

    private fun setupFilterButtons(view: View) {
        val languageButtons = arrayOf(
            view.findViewById<Button>(R.id.button_lang_python),
            view.findViewById<Button>(R.id.button_lang_java),
            view.findViewById<Button>(R.id.button_lang_kotlin),
            view.findViewById<Button>(R.id.button_lang_cpp),
            view.findViewById<Button>(R.id.button_lang_js)
        )

        val typeButtons = arrayOf(
            view.findViewById<Button>(R.id.button_type_ml),
            view.findViewById<Button>(R.id.button_type_web),
            view.findViewById<Button>(R.id.button_type_mobile),
            view.findViewById<Button>(R.id.button_type_blockchain),
            view.findViewById<Button>(R.id.button_type_game)
        )

        languageButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                filterProjectsByLanguage(languages[index])
            }
        }

        typeButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                filterProjectsByType(types[index])
            }
        }
    }

    private fun filterProjectsByLanguage(language: String) {
        val filteredProjects = projects.filter { it.language == language }
        adapter = ProjectsAdapter2(filteredProjects) { project ->
            // 프로젝트 아이템 클릭 이벤트 처리
        }
        view?.findViewById<RecyclerView>(R.id.recycler_view_projects)?.adapter = adapter
    }

    private fun filterProjectsByType(type: String) {
        val filteredProjects = projects.filter { it.type == type }
        adapter = ProjectsAdapter2(filteredProjects) { project ->
            // 프로젝트 아이템 클릭 이벤트 처리
        }
        view?.findViewById<RecyclerView>(R.id.recycler_view_projects)?.adapter = adapter
    }

    private fun loadProjects(): List<Project> {
        // 프로젝트 데이터 로드 (예: JSON 파일, 데이터베이스 등)
        return listOf(
            Project("Project 1", "2023-01-01", "2023-06-01", "Python", "Machine Learning", "Contents 1", likes = 5),
            Project("Project 2", "2023-02-01", null, "Java", "Web Development", "Contents 2", likes = 3),
            // 더 많은 프로젝트 데이터
        )
    }
}
