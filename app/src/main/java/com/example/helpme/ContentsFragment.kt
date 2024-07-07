package com.example.helpme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.graphics.Rect
import androidx.activity.OnBackPressedCallback
import com.example.helpme.network.Project

class ContentsFragment : Fragment() {

    private lateinit var project: Project
    private lateinit var editTextContents: EditText
    private lateinit var textViewContents: TextView
    private var rootView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contents, container, false)
        rootView = view

        project = arguments?.getParcelable("project") ?: Project("", "", "", "", "", "", "", "")

        editTextContents = view.findViewById(R.id.edit_text_contents)
        textViewContents = view.findViewById(R.id.text_view_contents)

        // 초기 텍스트 설정 및 ViewText 모드로 시작
        textViewContents.text = project.contents
        toggleViewMode(false)

        // EditText에 포커스를 잃으면 자동 저장 및 TextView로 전환
        editTextContents.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveContents()
                toggleViewMode(false)
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

        // TextView 클릭 시 EditText로 전환 및 키보드 표시
        textViewContents.setOnClickListener {
            toggleViewMode(true)
            editTextContents.requestFocus()
            showKeyboard(editTextContents)
        }

        // 키보드 상태 변경 리스너 설정
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            view.getWindowVisibleDisplayFrame(r)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight < screenHeight * 0.15) { // 키보드가 사라졌을 때
                saveContents()
                toggleViewMode(false)
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뒤로가기 버튼 처리
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (editTextContents.visibility == View.VISIBLE) {
                    saveContents()
                    toggleViewMode(false)
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun saveContents() {
        // 프로젝트 내용을 저장하는 로직을 구현합니다.
        project.contents = editTextContents.text.toString()
        Toast.makeText(context, "Contents saved", Toast.LENGTH_SHORT).show()
    }

    private fun toggleViewMode(isEditMode: Boolean) {
        if (isEditMode) {
            editTextContents.visibility = View.VISIBLE
            textViewContents.visibility = View.GONE
            editTextContents.setText(project.contents)
            editTextContents.setSelection(editTextContents.text.length)
        } else {
            editTextContents.visibility = View.GONE
            textViewContents.visibility = View.VISIBLE
            textViewContents.text = project.contents
        }
    }

    private fun showKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
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
