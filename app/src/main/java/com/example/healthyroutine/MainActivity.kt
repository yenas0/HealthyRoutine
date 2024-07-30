package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val kakaoLoginImageView = findViewById<ImageView>(R.id.kakaoLoginImageView)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)

        // Set up the login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            // Navigate to HomeActivity
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish() // Call finish() to prevent returning to the login screen
                        } else {
                            Toast.makeText(this, "다시 입력해주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "id 또는 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the Kakao login button click listener
        kakaoLoginImageView.setOnClickListener {
            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                if (error != null) {
                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                        UserApiClient.instance.loginWithKakaoTalk(this, callback = mCallback)
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback)
                    }
                } else if (token != null) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        // Set up the sign-up text view click listener
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
        } else if (token != null) {
            Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
            // Authenticate with Firebase
            val credential = KakaoAuthProvider.getCredential(token.accessToken)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Firebase 인증 성공", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Firebase 인증 실패", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}