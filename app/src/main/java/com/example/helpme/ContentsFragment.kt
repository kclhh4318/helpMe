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
    private lateinit var editTextRemember: EditText
    private lateinit var editTextReference: EditText
    private lateinit var textViewRemember: TextView
    private lateinit var textViewReference: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contents, container, false)

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", "", false, "", "")

        editTextRemember = view.findViewById(R.id.edit_text_remember)
        editTextReference = view.findViewById(R.id.edit_text_reference)
        textViewRemember = view.findViewById(R.id.text_view_remember)
        textViewReference = view.findViewById(R.id.text_view_reference)

        // 초기 텍스트 설정
        editTextRemember.setText(project.remember)
        textViewRemember.text = project.remember
        editTextReference.setText(project.reference)
        textViewReference.text = project.reference

        // EditText에 포커스를 잃으면 자동 저장 및 TextView로 전환
        editTextRemember.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveRememberContents()
                toggleRememberViewMode(false)
            }
        }

        editTextReference.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveReferenceContents()
                toggleReferenceViewMode(false)
            }
        }

        // 키보드에서 완료 버튼을 누르면 자동 저장 및 TextView로 전환
        editTextRemember.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveRememberContents()
                toggleRememberViewMode(false)
                true
            } else {
                false
            }
        }

        editTextReference.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveReferenceContents()
                toggleReferenceViewMode(false)
                true
            } else {
                false
            }
        }

        // 텍스트가 변경될 때마다 프로젝트 객체에 저장
        editTextRemember.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project.remember = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        editTextReference.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                project.reference = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // TextView 클릭 시 EditText로 전환
        textViewRemember.setOnClickListener {
            toggleRememberViewMode(true)
        }

        textViewReference.setOnClickListener {
            toggleReferenceViewMode(true)
        }

        return view
    }

    private fun saveRememberContents() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        Toast.makeText(context, "Remember contents saved", Toast.LENGTH_SHORT).show()
    }

    private fun saveReferenceContents() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        Toast.makeText(context, "Reference contents saved", Toast.LENGTH_SHORT).show()
    }

    private fun toggleRememberViewMode(isEditMode: Boolean) {
        if (isEditMode) {
            editTextRemember.visibility = View.VISIBLE
            textViewRemember.visibility = View.GONE
            editTextRemember.requestFocus()
        } else {
            editTextRemember.visibility = View.GONE
            textViewRemember.visibility = View.VISIBLE
            textViewRemember.text = editTextRemember.text.toString()
        }
    }

    private fun toggleReferenceViewMode(isEditMode: Boolean) {
        if (isEditMode) {
            editTextReference.visibility = View.VISIBLE
            textViewReference.visibility = View.GONE
            editTextReference.requestFocus()
        } else {
            editTextReference.visibility = View.GONE
            textViewReference.visibility = View.VISIBLE
            textViewReference.text = editTextReference.text.toString()
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
