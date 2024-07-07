package com.example.helpme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ExploreFragment : Fragment() {

    private lateinit var nickname: String
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        val activity = activity as MainActivity
        nickname = activity.intent.getStringExtra("nickname") ?: "No Nickname"
        email = activity.intent.getStringExtra("email") ?: "No Email"

        val textView: TextView = view.findViewById(R.id.text_explore)
        textView.text = "Nickname: $nickname\nEmail: $email"

        return view
    }
}
