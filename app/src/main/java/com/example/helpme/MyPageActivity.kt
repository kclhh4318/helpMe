package com.example.helpme

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.helpme.model.Info
import com.example.helpme.network.ApiService
import com.example.helpme.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var nickname: String
    private lateinit var email: String
    private lateinit var profileImage: String

    companion object {
        private const val TAG = "MyPageActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)

        nickname = intent.getStringExtra("nickname") ?: "No Nickname"
        email = intent.getStringExtra("email") ?: "No Email"
        profileImage = intent.getStringExtra("profile_image") ?: ""

        // Log the email
        Log.d(TAG, "사용자 이메일: $email")

        // Initialize Views
        val profileImageView = findViewById<ImageView>(R.id.profile_image)
        val nicknameTextView = findViewById<TextView>(R.id.nickname)
        val emailTextView = findViewById<TextView>(R.id.email)
        val exitIcon = findViewById<ImageView>(R.id.ic_exit)

        // Set profile data
        Glide.with(this).load(profileImage).placeholder(R.drawable.ic_profile_placeholder).into(profileImageView)
        nicknameTextView.text = nickname
        emailTextView.text = email

        // Load info from server
        loadInfoFromServer()

        // Exit functionality
        exitIcon.setOnClickListener {
            finish()
        }
    }

    private fun loadInfoFromServer() {
        val apiService = RetrofitClient.instance.create(ApiService::class.java)
        apiService.getUserInfo(email).enqueue(object : Callback<List<Info>> {
            override fun onResponse(call: Call<List<Info>>, response: Response<List<Info>>) {
                Log.d(TAG, "서버 응답 코드: ${response.code()}")
                Log.d(TAG, "서버 응답 헤더: ${response.headers()}")
                Log.d(TAG, "서버 응답 바디: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let { infoList ->
                        if (infoList.isNotEmpty()) {
                            val info = infoList[0]
                            updateInfoUI(info)
                            // Log the received Info
                            Log.d(TAG, "받은 Info 정보: $info")
                        } else {
                            Log.w(TAG, "사용자 정보가 비어있습니다.")
                            Toast.makeText(this@MyPageActivity, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e(TAG, "서버 응답 실패: ${response.code()}")
                    Toast.makeText(this@MyPageActivity, "정보를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Info>>, t: Throwable) {
                Log.e(TAG, "네트워크 오류 발생", t)
                Toast.makeText(this@MyPageActivity, "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateInfoUI(info: Info) {
        findViewById<TextView>(R.id.tv_prog).text = "${info.end_d_not_null_count}"
        findViewById<TextView>(R.id.tv_complete).text = "${info.end_d_not_null_count}"
        findViewById<TextView>(R.id.tv_total_likes).text = "${info.total_likes}"
        findViewById<TextView>(R.id.tv_most_lan).text = info.most_common_lan ?: "Unknown"
        findViewById<TextView>(R.id.tv_most_type).text = info.most_common_type ?: "Unknown"
    }
}