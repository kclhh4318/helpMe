package com.example.helpme

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.helpme.model.Project
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MyLearningFragment : Fragment() {

    private lateinit var nickname: String
    private lateinit var email: String
    private lateinit var profileImage: String
    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectsAdapter

    private val languages = arrayOf("Python", "Java", "Kotlin", "C++", "JavaScript")
    private val types = arrayOf("Machine Learning", "Web Development", "Mobile Development", "Blockchain", "Game Development")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_learning, container, false)

        val activity = activity as MainActivity
        nickname = activity.intent.getStringExtra("nickname") ?: "No Nickname"
        email = activity.intent.getStringExtra("email") ?: "No Email"
        profileImage = activity.intent.getStringExtra("profile_image") ?: ""

        // 프로필 설정
        val profileSection: LinearLayout = view.findViewById(R.id.profile_section)
        val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        val nicknameTextView: TextView = view.findViewById(R.id.text_my_learning)
        val mostUsedTextView: TextView = view.findViewById(R.id.text_most_used)

        nicknameTextView.text = "$nickname 님, 어서오세요!"
        mostUsedTextView.text = "가장 열심히 공부하고 있어요!"
        Glide.with(this)
            .load(profileImage)
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(profileImageView)

        // 프로필 섹션 클릭 리스너 설정
        profileSection.setOnClickListener {
            val intent = Intent(activity, MyPageActivity::class.java).apply {
                putExtra("nickname", nickname)
                putExtra("email", email)
                putExtra("profile_image", profileImage)
            }
            startActivity(intent)
        }

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        projects = mutableListOf()
        adapter = ProjectsAdapter(projects, { project: Project? ->
            if (project == null) {
                showAddProjectDialog()
            } else {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("proj_id", project.proj_id)
                    putExtra("currentUserEmail", email)
                    putExtra("title", project.title)
                    putExtra("start_d", project.start_d)
                    putExtra("end_d", project.end_d)
                    putExtra("lan", project.lan)
                    putExtra("type", project.type)
                }
                startActivity(intent)
            }
        }, { project: Project ->
            showEditProjectDialog(project)
        })
        recyclerView.adapter = adapter

        // 서버로부터 프로젝트 데이터 가져오기
        loadProjectsFromServer()

        return view
    }

    private fun loadProjectsFromServer() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getUserProjects(email).enqueue(object : Callback<List<Project>> {
            override fun onResponse(call: Call<List<Project>>, response: Response<List<Project>>) {
                if (response.isSuccessful) {
                    projects.clear()
                    Log.d("API_RESPONSE", "Raw JSON: ${response.body()}")
                    Log.d("API_CALL", "프로젝트 데이터 불러오기 성공")
                    response.body()?.let {
                        projects.addAll(it)
                        Log.d("API_CALL", "받아온 프로젝트 데이터: ${it.joinToString("\n")}")
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "프로젝트를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Project>>, t: Throwable) {
                Log.e("API_CALL", "네트워크 오류 발생", t)
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddProjectDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_project, null)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Add Project")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        val editTextTitle: EditText = dialogView.findViewById(R.id.edit_text_project_title)
        val buttonStartDate: Button = dialogView.findViewById(R.id.button_start_date)
        val buttonEndDate: Button = dialogView.findViewById(R.id.button_end_date)
        val spinnerLanguage: Spinner = dialogView.findViewById(R.id.spinner_language)
        val spinnerType: Spinner = dialogView.findViewById(R.id.spinner_type)
        val buttonAddProject: Button = dialogView.findViewById(R.id.button_add_project)

        // 스피너 설정
        spinnerLanguage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)

        // 날짜 선택기 설정
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var startDate: String? = null
        var endDate: String? = null

        buttonStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                startDate = dateFormat.format(calendar.time)
                buttonStartDate.text = startDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        buttonEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                endDate = dateFormat.format(calendar.time)
                buttonEndDate.text = endDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 프로젝트 추가 버튼 클릭 이벤트 설정
        buttonAddProject.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val language = spinnerLanguage.selectedItem as String
            val type = spinnerType.selectedItem as String

            if (title.isNotEmpty() && startDate != null) {
                val newProject = Project(
                    proj_id = 0, // 새 프로젝트의 경우, 서버에서 자동으로 ID가 할당될 것입니다.
                    title = title,
                    start_d = startDate!!,
                    end_d = endDate,
                    lan = language,
                    type = type,
                    email = email
                )
                // 서버로 새로운 프로젝트 데이터 전송
                addProjectToServer(newProject)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun addProjectToServer(newProject: Project) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.createProject(newProject).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 서버에서 프로젝트 목록을 다시 불러오는 대신 로컬 리스트에 추가하고 Adapter에 알림
                    loadProjectsFromServer()
                } else {
                    Toast.makeText(context, "프로젝트 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showEditProjectDialog(project: Project) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_project, null)
        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit Project")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        val editTextTitle: EditText = dialogView.findViewById(R.id.edit_text_project_title)
        val buttonStartDate: Button = dialogView.findViewById(R.id.button_start_date)
        val buttonEndDate: Button = dialogView.findViewById(R.id.button_end_date)
        val spinnerLanguage: Spinner = dialogView.findViewById(R.id.spinner_language)
        val spinnerType: Spinner = dialogView.findViewById(R.id.spinner_type)
        val buttonAddProject: Button = dialogView.findViewById(R.id.button_add_project)

        // 스피너 설정
        spinnerLanguage.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        spinnerType.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)

        // 기존 프로젝트 정보 채우기
        editTextTitle.setText(project.title)
        buttonStartDate.text = project.start_d
        buttonEndDate.text = project.end_d ?: "진행 중"
        spinnerLanguage.setSelection(languages.indexOf(project.lan))
        spinnerType.setSelection(types.indexOf(project.type))

        // 날짜 선택기 설정
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        var startDate: String? = project.start_d
        var endDate: String? = project.end_d

        buttonStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                startDate = dateFormat.format(calendar.time)
                buttonStartDate.text = startDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        buttonEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                endDate = dateFormat.format(calendar.time)
                buttonEndDate.text = endDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // 프로젝트 수정 버튼 클릭 이벤트 설정
        buttonAddProject.text = "Save"
        buttonAddProject.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val language = spinnerLanguage.selectedItem as String
            val type = spinnerType.selectedItem as String

            if (title.isNotEmpty() && startDate != null) {
                val updatedProject = project.copy(
                    title = title,
                    start_d = startDate!!,
                    end_d = endDate,
                    lan = language,
                    type = type
                )
                // 서버로 수정된 프로젝트 데이터 전송
                updateProjectOnServer(updatedProject)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateProjectOnServer(project: Project) {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.updateProject(project.proj_id, project).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 서버에서 프로젝트 목록을 다시 불러오는 대신 로컬 리스트에서 수정하고 Adapter에 알림
                    loadProjectsFromServer()
                } else {
                    Toast.makeText(context, "프로젝트 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
