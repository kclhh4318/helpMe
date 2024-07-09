package com.example.helpme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.helpme.model.ProjectDetail
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreFragment : Fragment() {

    private lateinit var projects: MutableList<ProjectDetail>
    private lateinit var adapter: ProjectsAdapter2
    private var currentUserEmail: String = "current_user_email@example.com" // 예시로 이메일을 설정합니다. 실제로는 로그인 정보를 사용하세요.

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
        loadProjectsFromServer()

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectsAdapter2(requireContext(), mutableListOf()) { project ->
            project?.let {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("proj_id", it.proj_id)
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
        if (!::projects.isInitialized) {
            projects = mutableListOf()
        }
        val filteredProjects = projects.filter {
            (selectedLanguage == null || it.lan == selectedLanguage) &&
                    (selectedType == null || it.type == selectedType)
        }
        adapter.updateProjects(filteredProjects)
    }

    private fun loadProjectsFromServer() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getAllProjects().enqueue(object : Callback<List<ProjectDetail>> {
            override fun onResponse(call: Call<List<ProjectDetail>>, response: Response<List<ProjectDetail>>) {
                if (response.isSuccessful) {
                    projects = response.body()?.sortedByDescending { it.likes }?.toMutableList() ?: mutableListOf()
                    adapter.updateProjects(projects)
                } else {
                    Toast.makeText(context, "프로젝트를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProjectDetail>>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROJECT_DETAIL && resultCode == AppCompatActivity.RESULT_OK) {
            data?.getParcelableExtra<ProjectDetail>("updatedProject")?.let { updatedProject ->
                val index = projects.indexOfFirst { it.proj_id == updatedProject.proj_id }
                if (index != -1) {
                    projects[index] = updatedProject
                    adapter.notifyItemChanged(index)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_PROJECT_DETAIL = 1
    }
}
