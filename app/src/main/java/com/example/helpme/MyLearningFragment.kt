package com.example.helpme

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

class MyLearningFragment : Fragment() {

    private lateinit var nickname: String
    private lateinit var profileImage: String
    private lateinit var projects: MutableList<Project>
    private lateinit var adapter: ProjectsAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private val languages = arrayOf("Python", "Java", "Kotlin", "C++", "JavaScript")
    private val types = arrayOf("Machine Learning", "Web Development", "Mobile Development", "Blockchain", "Game Development")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_learning, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("projects_pref", Context.MODE_PRIVATE)

        val activity = activity as MainActivity
        nickname = activity.intent.getStringExtra("nickname") ?: "No Nickname"
        profileImage = activity.intent.getStringExtra("profile_image") ?: ""

        // JSON 데이터 로드
        projects = loadProjects().filter { it.endDate == null }.toMutableList()

        // 가장 많이 사용하는 언어와 개발 타입
        val mostUsedLanguage = projects.groupBy { it.language }.maxByOrNull { it.value.size }?.key ?: "N/A"
        val mostUsedType = projects.groupBy { it.type }.maxByOrNull { it.value.size }?.key ?: "N/A"

        // 프로필 설정
        val profileImageView: ImageView = view.findViewById(R.id.profile_image)
        val nicknameTextView: TextView = view.findViewById(R.id.text_my_learning)
        val mostUsedTextView: TextView = view.findViewById(R.id.text_most_used)

        nicknameTextView.text = "$nickname 님, 어서오세요!"
        mostUsedTextView.text = "$mostUsedLanguage, $mostUsedType\n가장 열심히 공부하고 있어요!"
        Glide.with(this)
            .load(profileImage)
            .placeholder(R.drawable.ic_profile_placeholder)
            .into(profileImageView)

        // 리사이클러뷰 설정
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_projects)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ProjectsAdapter(projects) { project ->
            if (project == null) {
                showAddProjectDialog()
            } else {
                val intent = Intent(activity, ProjectDetailActivity::class.java).apply {
                    putExtra("project", project)
                }
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter

        return view
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
                val newProject = Project(title, startDate!!, endDate, language, type, contents = "", isLiked = false)
                projects.add(newProject)
                adapter.notifyItemInserted(projects.size - 1)
                saveProjects()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun loadProjects(): MutableList<Project> {
        val json = sharedPreferences.getString("projects_list", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Project>>() {}.type
            Gson().fromJson(json, type)
        } else {
            loadJSONFromAsset().toMutableList()
        }
    }

    private fun saveProjects() {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(projects)
        editor.putString("projects_list", json)
        editor.apply()
    }

    private fun loadJSONFromAsset(): List<Project> {
        val json: String?
        try {
            val inputStream = context?.assets?.open("projects.json")
            val size = inputStream?.available()
            val buffer = ByteArray(size!!)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return emptyList()
        }

        val projectsList = mutableListOf<Project>()
        val jsonObject = JSONObject(json)
        val projectsArray = jsonObject.getJSONArray("projects")
        for (i in 0 until projectsArray.length()) {
            val project = projectsArray.getJSONObject(i)
            val title = project.getString("title")
            val startDate = project.getString("startDate")
            val endDate = project.optString("endDate", null)
            val language = project.getString("language")
            val type = project.getString("type")
            val contents = project.optString("contents", "")
            val references = project.optString("references", "")
            val remember = project.optString("remember", "")
            val isLiked = project.optBoolean("isLiked", false)
            projectsList.add(Project(title, startDate, endDate, language, type, contents, references, remember, isLiked))
        }

        return projectsList
    }
}
