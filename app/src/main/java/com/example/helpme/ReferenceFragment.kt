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
import com.example.helpme.databinding.FragmentReferenceBinding
import com.example.helpme.model.ProjectDetail

class ReferenceFragment : Fragment() {

    private lateinit var binding: FragmentReferenceBinding
    private lateinit var project: ProjectDetail
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReferenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        project = arguments?.getParcelable("project")!!

        binding.referenceTextView.text = project.ref
        binding.referenceEditText.setText(project.ref)

        binding.referenceTextView.setOnClickListener {
            toggleEditMode(true)
        }

        binding.referenceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                project.ref = s.toString()
                (activity as? ProjectDetailActivity)?.updateProjectDetail(project)
            }
        })

        binding.referenceEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                toggleEditMode(false)
            }
        }
    }

    private fun toggleEditMode(edit: Boolean) {
        isEditing = edit
        if (edit) {
            binding.referenceTextView.visibility = View.GONE
            binding.referenceEditText.visibility = View.VISIBLE
            binding.referenceEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.referenceEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.referenceTextView.visibility = View.VISIBLE
            binding.referenceEditText.visibility = View.GONE
            binding.referenceTextView.text = binding.referenceEditText.text.toString()
            project.ref = binding.referenceEditText.text.toString()
            (activity as? ProjectDetailActivity)?.updateProjectDetail(project)
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.referenceEditText.windowToken, 0)
        }
    }

    companion object {
        fun newInstance(project: ProjectDetail): ReferenceFragment {
            val fragment = ReferenceFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
