package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up) // Make sure the layout file name is activity_sign_up.xml

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val etId = findViewById<EditText>(R.id.et_id)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etPasswordConfirm = findViewById<EditText>(R.id.et_password_confirm)
        val etNickname = findViewById<EditText>(R.id.et_nickname)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)

        btnSignUp.setOnClickListener {
            val email = etId.text.toString()
            val password = etPassword.text.toString()
            val passwordConfirm = etPasswordConfirm.text.toString()
            val nickname = etNickname.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == passwordConfirm && nickname.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign up success
                            Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                            // Navigate back to MainActivity to log in
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign up fails, display a message to the user.
                            Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "입력 정보를 확인해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}