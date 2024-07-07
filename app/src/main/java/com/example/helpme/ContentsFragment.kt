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

class ContentsFragment : Fragment() {

    private lateinit var project: Project
    private lateinit var editTextContents: EditText
    private lateinit var textViewContents: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contents, container, false)

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", "", false)

        editTextContents = view.findViewById(R.id.edit_text_contents)
        textViewContents = view.findViewById(R.id.text_view_contents)

        // 초기 텍스트 설정
        editTextContents.setText(project.contents)
        textViewContents.text = project.contents

        // EditText에 포커스를 잃으면 자동 저장 및 TextView로 전환
        editTextContents.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveContents()
                toggleViewMode(false)
            }
        }

        // 키보드에서 완료 버튼을 누르면 자동 저장 및 TextView로 전환
        editTextContents.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveContents()
                toggleViewMode(false)
                true
            } else {
                false
            }
        }

        // 텍스트가 변경될 때마다 프로젝트 객체에 저장
        editTextContents.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project.contents = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // TextView 클릭 시 EditText로 전환
        textViewContents.setOnClickListener {
            toggleViewMode(true)
        }

        return view
    }

    private fun saveContents() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        Toast.makeText(context, "Contents saved", Toast.LENGTH_SHORT).show()
    }

    private fun toggleViewMode(isEditMode: Boolean) {
        if (isEditMode) {
            editTextContents.visibility = View.VISIBLE
            textViewContents.visibility = View.GONE
            editTextContents.requestFocus()
        } else {
            editTextContents.visibility = View.GONE
            textViewContents.visibility = View.VISIBLE
            textViewContents.text = editTextContents.text.toString()
        }
    }

    companion object {
        fun newInstance(project: Project): ContentsFragment {
            val fragment = ContentsFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
