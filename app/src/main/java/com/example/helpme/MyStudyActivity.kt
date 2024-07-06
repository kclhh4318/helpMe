package com.example.helpme

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityMyStudyBinding

class MyStudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyStudyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "MyStudyActivity onCreate 시작")

        // Retrieve the user information passed from AuthCodeHandlerActivity
        val nickname = intent.getStringExtra("nickname")
        val email = intent.getStringExtra("email")

        if (nickname != null && email != null) {
            binding.txtNickName.text = nickname
            binding.txtEmail.text = email
        } else {
            Log.e(TAG, "사용자 정보가 null입니다.")
        }
    }

    companion object {
        private const val TAG = "MyStudyActivity"
    }
}
