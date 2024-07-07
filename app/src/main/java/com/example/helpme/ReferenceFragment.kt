package com.example.helpme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.helpme.network.Project

class ReferenceFragment : Fragment() {

    private lateinit var project: Project
    private lateinit var editTextReferences: EditText
    private lateinit var textViewReferences: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reference, container, false)

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", "", "", "")

        editTextReferences = view.findViewById(R.id.edit_text_references)
        textViewReferences = view.findViewById(R.id.text_view_references)

        // 초기 텍스트 설정
        editTextReferences.setText(project.references)
        textViewReferences.text = project.references

        // EditText에 포커스를 잃으면 자동 저장 및 TextView로 전환
        editTextReferences.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveReferences()
                toggleViewMode(false)
            }
        }

        // 키보드에서 완료 버튼을 누르면 자동 저장 및 TextView로 전환
        editTextReferences.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveReferences()
                toggleViewMode(false)
                true
            } else {
                false
            }
        }

        // 텍스트가 변경될 때마다 프로젝트 객체에 저장
        editTextReferences.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project.references = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // TextView 클릭 시 EditText로 전환
        textViewReferences.setOnClickListener {
            toggleViewMode(true)
        }

        return view
    }

    private fun saveReferences() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        Toast.makeText(context, "References saved", Toast.LENGTH_SHORT).show()
    }

    private fun toggleViewMode(isEditMode: Boolean) {
        if (isEditMode) {
            editTextReferences.visibility = View.VISIBLE
            textViewReferences.visibility = View.GONE
            editTextReferences.requestFocus()
        } else {
            editTextReferences.visibility = View.GONE
            textViewReferences.visibility = View.VISIBLE
            textViewReferences.text = editTextReferences.text.toString()
        }
    }

    companion object {
        fun newInstance(project: Project): ReferenceFragment {
            val fragment = ReferenceFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
