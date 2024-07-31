package com.example.healthyroutine

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser!!

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val nicknameEditText: EditText = findViewById(R.id.nicknameEditText)

        // 사용자 정보 설정
        emailEditText.setText(currentUser.email)
        loadUserProfile(nicknameEditText)

        val updateProfileButton: Button = findViewById(R.id.updateProfileButton)
        updateProfileButton.setOnClickListener {
            updateProfile(nicknameEditText)
        }
    }

    private fun loadUserProfile(nicknameEditText: EditText) {
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("nickname")
                    nicknameEditText.setText(nickname)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EditProfileActivity", "Error getting documents: ", exception)
            }
    }

    private fun updateProfile(nicknameEditText: EditText) {
        val currentPasswordEditText: EditText = findViewById(R.id.currentPasswordEditText)
        val newPasswordEditText: EditText = findViewById(R.id.newPasswordEditText)
        val confirmNewPasswordEditText: EditText = findViewById(R.id.confirmNewPasswordEditText)

        val currentPassword = currentPasswordEditText.text.toString()
        val newPassword = newPasswordEditText.text.toString()
        val confirmNewPassword = confirmNewPasswordEditText.text.toString()
        val nickname = nicknameEditText.text.toString()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty() || nickname.isEmpty()) {
            Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmNewPassword) {
            Toast.makeText(this, "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
        currentUser.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        updateNickname(nickname)
                    } else {
                        Toast.makeText(this, "비밀번호 변경에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateNickname(nickname: String) {
        val userRef = firestore.collection("users").document(currentUser.uid)
        userRef.update("nickname", nickname)
            .addOnSuccessListener {
                Toast.makeText(this, "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "회원 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.w("EditProfileActivity", "Error updating document", e)
            }
    }
}
