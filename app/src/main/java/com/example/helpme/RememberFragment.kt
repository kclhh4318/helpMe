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
import com.example.helpme.databinding.FragmentRememberBinding
import com.example.helpme.model.ProjectDetail
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RememberFragment : Fragment() {

    private lateinit var binding: FragmentRememberBinding
    private lateinit var project: ProjectDetail
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRememberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        project = arguments?.getParcelable("project")!!

        binding.rememberTextView.text = project.remember
        binding.rememberEditText.setText(project.remember)

        binding.rememberTextView.setOnClickListener {
            toggleEditMode(true)
        }

        binding.rememberEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                project.remember = s.toString()
            }
        })

        binding.rememberEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                toggleEditMode(false)
            }
        }
    }

    private fun toggleEditMode(edit: Boolean) {
        isEditing = edit
        if (edit) {
            binding.rememberTextView.visibility = View.GONE
            binding.rememberEditText.visibility = View.VISIBLE
            binding.rememberEditText.requestFocus()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.rememberEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            binding.rememberTextView.visibility = View.VISIBLE
            binding.rememberEditText.visibility = View.GONE
            binding.rememberTextView.text = binding.rememberEditText.text.toString()
            project.remember = binding.rememberEditText.text.toString()
            // updateProjectContents()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.rememberEditText.windowToken, 0)
        }
    }

    /*
    private fun updateProjectContents() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.updateProjectContents(project).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (!response.isSuccessful) {
                    // 오류 처리
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 오류 처리
            }
        })
    }
    */

    companion object {
        fun newInstance(project: ProjectDetail): RememberFragment {
            val fragment = RememberFragment()
            val args = Bundle()
            args.putParcelable("project", project)
            fragment.arguments = args
            return fragment
        }
    }
}
