package com.example.helpme

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.helpme.databinding.ActivityMyStudyBinding
import androidx.core.app.ActivityCompat


class MyStudyActivity : AppCompatActivity() {

    var backPressedTime: Long = 0

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

    //로그인 후 뜨는 메인 화면들이 사실상 해당 액티비티가 실질적인 메인화면이므로, 여기서 뒤로가기를 누르면 어플이 종료되는 걸로 하자.
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime >= 1500) {
            backPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 눌러서 종료하세요.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this)
            System.runFinalization()
            System.exit(0)
        }
    }

    companion object {
        private const val TAG = "MyStudyActivity"
    }
}
