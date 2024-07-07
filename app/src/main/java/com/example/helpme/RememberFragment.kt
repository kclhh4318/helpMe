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

class RememberFragment : Fragment() {

    private lateinit var project: Project
    private lateinit var editTextRemember: EditText
    private lateinit var textViewRemember: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_remember, container, false)

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", "", "", "")

        editTextRemember = view.findViewById(R.id.edit_text_remember)
        textViewRemember = view.findViewById(R.id.text_view_remember)

        // 초기 텍스트 설정
        editTextRemember.setText(project.remember)
        textViewRemember.text = project.remember

        // EditText에 포커스를 잃으면 자동 저장 및 TextView로 전환
        editTextRemember.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveRemember()
                toggleViewMode(false)
            }
        }

        // 키보드에서 완료 버튼을 누르면 자동 저장 및 TextView로 전환
        editTextRemember.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                saveRemember()
                toggleViewMode(false)
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

        // TextView 클릭 시 EditText로 전환
        textViewRemember.setOnClickListener {
            toggleViewMode(true)
        }

        return view
    }

    private fun saveRemember() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        Toast.makeText(context, "Remember saved", Toast.LENGTH_SHORT).show()
    }

    private fun toggleViewMode(isEditMode: Boolean) {
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

    companion object {
        fun newInstance(project: Project): RememberFragment {
            val fragment = RememberFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
