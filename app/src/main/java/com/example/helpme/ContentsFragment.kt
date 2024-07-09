package com.example.helpme

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.helpme.databinding.FragmentContentsBinding
import com.example.helpme.model.ProjectDetail

class ContentsFragment : Fragment() {

    private lateinit var binding: FragmentContentsBinding
    private lateinit var project: ProjectDetail
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        project = arguments?.getParcelable("project")!!

        binding.contentsTextView.text = project.contents
        binding.contentsEditText.setText(project.contents)

        binding.contentsTextView.setOnClickListener {
            toggleEditMode(true)
        }

        binding.contentsEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                project.contents = s.toString()
                (activity as? ProjectDetailActivity)?.updateProjectDetail(project)
            }
        })

        binding.contentsEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                toggleEditMode(false)
            }
        }
    }

    private fun toggleEditMode(edit: Boolean) {
        isEditing = edit
        if (edit) {
            binding.contentsTextView.visibility = View.GONE
            binding.contentsEditText.visibility = View.VISIBLE
            binding.contentsEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.contentsEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.contentsTextView.visibility = View.VISIBLE
            binding.contentsEditText.visibility = View.GONE
            binding.contentsTextView.text = binding.contentsEditText.text.toString()
            project.contents = binding.contentsEditText.text.toString()
            (activity as? ProjectDetailActivity)?.updateProjectDetail(project)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.contentsEditText.windowToken, 0)
        }
    }

    companion object {
        fun newInstance(project: ProjectDetail): ContentsFragment {
            val fragment = ContentsFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
