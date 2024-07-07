package com.example.helpme

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import android.content.Intent
import com.example.helpme.network.User

class AuthCodeHandlerActivity : AppCompatActivity() {

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "로그인 실패 $error")
        } else if (token != null) {
            Log.e(TAG, "로그인 성공 ${token.accessToken}")
            // 사용자 정보 요청
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패 $error")
                } else if (user != null) {
                    Log.e(TAG, "사용자 정보 요청 성공: ${user.kakaoAccount?.profile?.nickname}")

                    // 사용자 정보 직접 전달
                    val userToSend = User(
                        id = user.id.toString(),
                        email = user.kakaoAccount?.email ?: "",
                        nickname = user.kakaoAccount?.profile?.nickname ?: ""
                    )

                    // 사용자 정보 전달
                    val intent = Intent(this@AuthCodeHandlerActivity, MainActivity::class.java).apply {
                        putExtra("nickname", userToSend.nickname)
                        putExtra("email", userToSend.email)
                        putExtra("profile_image", user.kakaoAccount?.profile?.thumbnailImageUrl)
                    }
                    Log.d(TAG, "MainActivity로 전환 시작")
                    startActivity(intent)
                    finish()  // AuthCodeHandlerActivity 종료
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AuthCodeHandlerActivity onCreate 시작")

        // 카카오톡 설치 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            Log.d(TAG, "카카오톡 설치 확인 완료")
            // 카카오톡 로그인
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    Log.e(TAG, "로그인 실패 $error")
                    // 사용자가 취소
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
                    }
                } else if (token != null) {
                    mCallback(token, null)
                }
            }
        } else {
            Log.d(TAG, "카카오톡 설치되지 않음, 카카오 이메일 로그인 시도")
            UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
        }
    }
}
