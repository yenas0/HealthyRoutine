package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize UI components
        val etId = findViewById<EditText>(R.id.et_id)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val etPasswordConfirm = findViewById<EditText>(R.id.et_password_confirm)
        val etNickname = findViewById<EditText>(R.id.et_nickname)
        val btnCheck = findViewById<Button>(R.id.btn_check)
        val btnSignUp = findViewById<Button>(R.id.btn_signup)
        val cbAllAgree = findViewById<CheckBox>(R.id.cb_all_agree)
        val cbTermsAgree = findViewById<CheckBox>(R.id.cb_terms_agree)
        val cbPrivacyAgree = findViewById<CheckBox>(R.id.cb_privacy_agree)
        val cbMarketingAgree = findViewById<CheckBox>(R.id.cb_marketing_agree)

        btnCheck.setOnClickListener {
            val email = etId.text.toString()

            if (email.isEmpty()) {
                etId.error = "이메일을 입력해주세요"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etId.error = "올바른 이메일 형식을 입력해주세요"
            } else {
                checkEmailExists(email) { exists ->
                    if (exists) {
                        etId.error = "이미 사용중인 이메일입니다"
                    } else {
                        etId.error = null
                        Toast.makeText(this, "사용 가능한 이메일입니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        cbAllAgree.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                cbTermsAgree.isChecked = true
                cbPrivacyAgree.isChecked = true
                cbMarketingAgree.isChecked = true
            } else {
                cbTermsAgree.isChecked = false
                cbPrivacyAgree.isChecked = false
                cbMarketingAgree.isChecked = false
            }
        }

        btnSignUp.setOnClickListener {
            val email = etId.text.toString()
            val password = etPassword.text.toString()
            val passwordConfirm = etPasswordConfirm.text.toString()
            val nickname = etNickname.text.toString()

            if (email.isEmpty()) {
                etId.error = "이메일을 입력해주세요"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etId.error = "올바른 이메일 형식을 입력해주세요"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = "비밀번호를 입력해주세요"
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                etPassword.error = "비밀번호는 8자 이상이어야 하며, 영문과 숫자를 포함해야 합니다"
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                etPasswordConfirm.error = "비밀번호가 일치하지 않습니다"
                return@setOnClickListener
            }

            if (nickname.isEmpty()) {
                etNickname.error = "닉네임을 입력해주세요"
                return@setOnClickListener
            }

            if (!cbTermsAgree.isChecked || !cbPrivacyAgree.isChecked || !cbMarketingAgree.isChecked) {
                Toast.makeText(this, "모든 약관에 동의해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkEmailExists(email) { exists ->
                if (exists) {
                    etId.error = "이미 사용중인 이메일입니다"
                } else {
                    createUser(email, password)
                }
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun checkEmailExists(email: String, callback: (Boolean) -> Unit) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList()
                callback(signInMethods.isNotEmpty())
            } else {
                Toast.makeText(this, "이메일 중복 확인 중 오류 발생: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }
    }

    private fun createUser(email: String, password: String) {
        val nickname = findViewById<EditText>(R.id.et_nickname).text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user?.uid

                    if (userId != null) {
                        val db = FirebaseFirestore.getInstance()
                        val userMap = hashMapOf(
                            "email" to email,
                            "nickname" to nickname
                        )

                        db.collection("users").document(userId).set(userMap)
                            .addOnSuccessListener {
                                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_SHORT).show()
                                // Navigate back to MainActivity to log in
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
