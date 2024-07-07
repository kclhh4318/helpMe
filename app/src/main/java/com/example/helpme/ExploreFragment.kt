package com.example.helpme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.StandardCharsets

class ExploreFragment : Fragment() {

    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectsAdapter2

    private val languages = arrayOf("Python", "Java", "Kotlin", "C++", "JavaScript")
    private val types = arrayOf("Machine Learning", "Web Development", "Mobile Development", "Blockchain", "Game Development")
    private var selectedLanguage: String? = null
    private var selectedType: String? = null

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
            project?.let {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("project", it)
                }
                startActivity(intent)
            }
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
                val language = languages[index]
                if (selectedLanguage == language) {
                    selectedLanguage = null
                    button.setTypeface(null, android.graphics.Typeface.NORMAL)
                } else {
                    selectedLanguage = language
                    languageButtons.forEach { it.setTypeface(null, android.graphics.Typeface.NORMAL) }
                    button.setTypeface(null, android.graphics.Typeface.BOLD)
                }
                filterProjects()
            }
        }

        typeButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                val type = types[index]
                if (selectedType == type) {
                    selectedType = null
                    button.setTypeface(null, android.graphics.Typeface.NORMAL)
                } else {
                    selectedType = type
                    typeButtons.forEach { it.setTypeface(null, android.graphics.Typeface.NORMAL) }
                    button.setTypeface(null, android.graphics.Typeface.BOLD)
                }
                filterProjects()
            }
        }
    }

    private fun filterProjects() {
        val filteredProjects = projects.filter {
            (selectedLanguage == null || it.language == selectedLanguage) &&
                    (selectedType == null || it.type == selectedType)
        }
        adapter = ProjectsAdapter2(filteredProjects) { project ->
            project?.let {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("project", it)
                }
                startActivity(intent)
            }
        }
        view?.findViewById<RecyclerView>(R.id.recycler_view_projects)?.adapter = adapter
    }

    private fun loadProjects(): List<Project> {
        val json: String?
        try {
            val inputStream = context?.assets?.open("projects.json")
            val size = inputStream?.available()
            val buffer = ByteArray(size!!)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return emptyList()
        }

        val type = object : TypeToken<List<Project>>() {}.type
        return Gson().fromJson(json, type)
    }
}
