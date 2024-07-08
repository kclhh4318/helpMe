package com.example.helpme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.nio.charset.StandardCharsets

class ExploreFragment : Fragment() {

    private lateinit var dbHelper: LikedProjectsDatabaseHelper
    private var currentUserEmail: String = ""
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

        dbHelper = LikedProjectsDatabaseHelper(requireContext())

        // 프로젝트 데이터 로드
        projects = loadProjects().toMutableList()

        // 현재 사용자 이메일 설정
        currentUserEmail = "current_user_email@example.com" // 예시로 이메일을 설정합니다. 실제로는 로그인 정보를 사용하세요.

        // 좋아요 상태 초기화
        projects.forEach { it.isLiked = dbHelper.isProjectLiked(it.title, currentUserEmail) }

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectsAdapter2(requireContext(), projects) { project ->
            project?.let {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("project", it)
                    putExtra("currentUserEmail", currentUserEmail)
                }
                startActivityForResult(intent, REQUEST_CODE_PROJECT_DETAIL)
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
        adapter = ProjectsAdapter2(requireContext(), projects) { project ->
            project?.let {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("project", it)
                    putExtra("currentUserEmail", currentUserEmail)
                    putExtra("isLiked", it.isLiked)  // 좋아요 상태를 전달합니다.
                    putExtra("likes", it.likes)      // 좋아요 개수를 전달합니다.
                }
                startActivityForResult(intent, REQUEST_CODE_PROJECT_DETAIL)
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
        val projects = Gson().fromJson<List<Project>>(json, type)

        // 각 프로젝트의 좋아요 상태와 개수를 데이터베이스에서 가져옵니다
        projects.forEach { project ->
            project.isLiked = dbHelper.isProjectLiked(project.title, currentUserEmail)
            project.likes = dbHelper.getProjectLikes(project.title)
        }

        return projects
    }

    // onActivityResult 메소드를 수정합니다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROJECT_DETAIL && resultCode == AppCompatActivity.RESULT_OK) {
            data?.getParcelableExtra<Project>("updatedProject")?.let { updatedProject ->
                val index = projects.indexOfFirst { it.title == updatedProject.title }
                if (index != -1) {
                    projects[index] = updatedProject
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }

    companion object {
        private const val REQUEST_CODE_PROJECT_DETAIL = 1
    }
}
