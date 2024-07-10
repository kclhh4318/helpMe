package com.example.helpme

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.helpme.network.RetrofitClient

class ExploreFragment : Fragment() {

    private lateinit var projects: MutableList<ProjectDetail>
    private lateinit var adapter: ProjectsAdapter2
    private var currentUserEmail: String = ""

    private val languages = arrayOf("Python", "Java", "Kotlin", "C++", "JavaScript")
    private val types = arrayOf("Machine Learning", "Web Development", "Mobile Development", "Blockchain", "Game Development")
    private var selectedLanguage: String? = null
    private var selectedType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        // MainActivity에서 전달받은 이메일 정보 가져오기
        currentUserEmail = arguments?.getString("email") ?: ""
        Log.d("ExploreFragment", "Received email: $currentUserEmail")

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
                    putExtra("projectOwnerEmail", it.email)
                    putExtra("title", it.title)
                    putExtra("start_d", it.start_d)
                    putExtra("end_d", it.end_d)
                    putExtra("lan", it.lan)
                    putExtra("type", it.type)
                }
                Log.d("ExploreFragment", "Sending currentUserEmail: $currentUserEmail")
                Log.d("ExploreFragment", "Sending projectOwnerEmail: ${it.email}")
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter

        // 필터 버튼 설정
        setupFilterButtons(view)

        return view
    }

    override fun onResume() {
        super.onResume()
        loadProjectsFromServer() // 프래그먼트가 다시 활성화될 때마다 데이터 로드
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
                val language = languages[index].uppercase()
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
                val type = types[index].uppercase()
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
        val filteredProjects = projects.filter { project ->
            (selectedLanguage == null || project.lan?.equals(selectedLanguage, ignoreCase = true) == true) &&
                    (selectedType == null || project.type?.replace(" ", "")?.equals(selectedType?.replace(" ", ""), ignoreCase = true) == true)
        }
        adapter.updateProjects(filteredProjects)
    }

    private fun loadProjectsFromServer() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getAllProjects().enqueue(object : Callback<List<ProjectDetail>> {
            override fun onResponse(call: Call<List<ProjectDetail>>, response: Response<List<ProjectDetail>>) {
                if (response.isSuccessful) {
                    projects = response.body()?.sortedByDescending { it.isLiked ?: false }?.toMutableList() ?: mutableListOf()
                    projects = response.body()?.sortedByDescending { it.likes ?: 0 }?.toMutableList() ?: mutableListOf()
                    adapter.updateProjects(projects)
                } else {
                    Toast.makeText(context, "프로젝트를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("ExploreFragment", "Error code: ${response.code()}, Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<ProjectDetail>>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("ExploreFragment", "Network error: ${t.message}", t)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PROJECT_DETAIL && resultCode == AppCompatActivity.RESULT_OK) {
            loadProjectsFromServer() // ProjectDetailActivity에서 돌아올 때마다 데이터 새로 고침
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val REQUEST_CODE_PROJECT_DETAIL = 1
    }
}
